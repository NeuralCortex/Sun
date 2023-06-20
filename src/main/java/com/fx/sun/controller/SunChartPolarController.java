package com.fx.sun.controller;

import com.fx.sun.Globals;
import com.fx.sun.pojo.EleTimePOJO;
import com.fx.sun.tools.StundenSchleifenThread;
import com.fx.sun.tools.SunChartPolarAnchorPage;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shredzone.commons.suncalc.SunPosition;

/**
 *
 * @author pscha
 */
public class SunChartPolarController implements Initializable, PopulateInterface {

    @FXML
    private BorderPane borderPane;
    @FXML
    private HBox hBox;
    @FXML
    private TextField tfLat;
    @FXML
    private TextField tfLon;
    @FXML
    private Button btnCalc;
    @FXML
    private Button btnTime;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField tfTime;

    @FXML
    private VBox vbBottom;
    @FXML
    private Slider sliderLat;
    @FXML
    private Slider sliderLon;

    private double lat;
    private double lon;

    private SunChartPolarAnchorPage sunChartPolarAnchorPage;

    private static final Logger _log = LogManager.getLogger(SunChartPolarController.class);
    private LocalDate now = LocalDate.now();

    private final boolean showSlider = Globals.SHOW_TEST_UI;

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        hBox.setId("hec-background-blue");

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        btnCalc.setText(bundle.getString("btn.calc.wgs"));

        dpDate.setValue(now);
        tfTime.setText("13:39:00");

        sliderLat.setMin(-90);
        sliderLat.setMax(90);
        sliderLat.setShowTickLabels(true);
        sliderLat.setShowTickMarks(true);
        sliderLat.setSnapToTicks(true);
        sliderLat.setBlockIncrement(1);
        sliderLat.setMajorTickUnit(10);

        sliderLon.setMin(-180);
        sliderLon.setMax(180);
        sliderLon.setShowTickLabels(true);
        sliderLon.setShowTickMarks(true);
        sliderLon.setSnapToTicks(true);
        sliderLon.setBlockIncrement(1);
        sliderLon.setMajorTickUnit(10);

        vbBottom.setVisible(showSlider);
        vbBottom.managedProperty().bind(vbBottom.visibleProperty());

        sunChartPolarAnchorPage = new SunChartPolarAnchorPage();
        borderPane.setCenter(sunChartPolarAnchorPage);

        btnCalc.setOnAction(e -> {
            lat = Double.valueOf(tfLat.getText());
            lon = Double.valueOf(tfLon.getText());
            setupAndRedraw();
        });

        btnTime.setOnAction(e -> {
            sunChartPolarAnchorPage.setEleTimePOJOMark(calcMark());
            sunChartPolarAnchorPage.redraw();
        });

        tfLat.setOnKeyPressed(e -> {
            getClipboardFromGoogleMaps(e);
        });

        tfLon.setOnKeyPressed(e -> {
            getClipboardFromGoogleMaps(e);
        });

        sliderLat.valueProperty().addListener((ov, o, n) -> {
            lat = n.doubleValue();
            tfLat.setText(lat + "");
            sunChartPolarAnchorPage.setEleTimePOJOMark(calcMark());
            setupAndRedraw();
        });

        sliderLon.valueProperty().addListener((ov, o, n) -> {
            lon = n.doubleValue();
            tfLon.setText(lon + "");
            sunChartPolarAnchorPage.setEleTimePOJOMark(calcMark());
            setupAndRedraw();
        });
    }

    private void getClipboardFromGoogleMaps(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == KeyCode.V) {
            try {
                String rawData = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                String wgsPos[] = rawData.split(",");
                tfLat.setText(wgsPos[0].trim());
                tfLon.setText(wgsPos[1].trim());
            } catch (Exception ex) {
                _log.error(ex.getMessage());
            }
        }
    }

    private EleTimePOJO calcMark() {
        LocalDate date = dpDate.getValue();

        String[] timeArray = tfTime.getText().split(":");
        int hour = Integer.valueOf(timeArray[0]);
        int min = Integer.valueOf(timeArray[1]);
        int sec = Integer.valueOf(timeArray[2]);

        if (lat > 0) {
            hour += 1;
        } else {
            hour += 2;
        }

        LocalDateTime localDateTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour, min, sec);
        SunPosition sunPosition = SunPosition.compute().timezone(TimeZone.getTimeZone("Egypt")).on(localDateTime).at(lat, lon).execute();
        double azi = sunPosition.getAzimuth();
        double alti = sunPosition.getAltitude();

        return new EleTimePOJO(0, azi, alti, localDateTime);
    }

    private HashMap<Integer, List<EleTimePOJO>> calcSchleifen() {
        HashMap<Integer, List<EleTimePOJO>> map = new HashMap<>();

        StundenSchleifenThread[] schleifenThreadArray = new StundenSchleifenThread[24];
        for (int i = 0; i < 24; i++) {
            StundenSchleifenThread thread = new StundenSchleifenThread(now, lat, lon, i, map);
            schleifenThreadArray[i] = thread;
        }
        for (int i = 0; i < schleifenThreadArray.length; i++) {
            schleifenThreadArray[i].start();
        }
        for (int i = 0; i < schleifenThreadArray.length; i++) {
            try {
                schleifenThreadArray[i].join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    private List<EleTimePOJO> calcSonnenStand(int year, int month, int day) {
        List<EleTimePOJO> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j++) {
                LocalDateTime localDateTime = LocalDateTime.of(year, month, day, i, j);
                SunPosition sunPosition = SunPosition.compute().on(localDateTime).at(lat, lon).execute();
                double azi = sunPosition.getAzimuth();
                double alti = sunPosition.getAltitude();
                list.add(new EleTimePOJO(0, azi, alti, localDateTime));
            }
        }
        list.sort(Comparator.comparing(t -> t.getAzimuth()));
        return list;
    }

    private HashMap<Integer, List<EleTimePOJO>> calcSonnenStandMap() {
        HashMap<Integer, List<EleTimePOJO>> map = new HashMap<>();
        int count = 0;
        map.put(count++, calcSonnenStand(now.getYear(), 6, 21));
        map.put(count++, calcSonnenStand(now.getYear(), 12, 21));
        for (int i = 1; i < 13; i++) {
            map.put(count++, calcSonnenStand(now.getYear(), i, 1));
        }
        return map;
    }

    private void setupAndRedraw() {
        calculate();
        sunChartPolarAnchorPage.redraw();
    }

    private void calculate() {
        sunChartPolarAnchorPage.setLat(lat);
        sunChartPolarAnchorPage.setLon(lon);
        sunChartPolarAnchorPage.setMapSchleifen(calcSchleifen());
        sunChartPolarAnchorPage.setMapSonnenStand(calcSonnenStandMap());
    }

    @Override
    public void populate() {
        if (borderPane.getCenter() == null) {
            sunChartPolarAnchorPage = new SunChartPolarAnchorPage();
            borderPane.setCenter(sunChartPolarAnchorPage);
        }

        lat = Double.valueOf(Globals.propman.getProperty(Globals.COORD_LAT));
        lon = Double.valueOf(Globals.propman.getProperty(Globals.COORD_LON));

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        sliderLat.setValue(lat);
        sliderLon.setValue(lon);

        setupAndRedraw();
    }

    @Override
    public void clear() {
        borderPane.setCenter(null);
    }
}
