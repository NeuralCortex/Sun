package com.fx.sun.controller.tabs;

import com.fx.sun.Globals;
import com.fx.sun.controller.PopulateInterface;
import com.fx.sun.pojo.EleTimePOJO;
import com.fx.sun.tools.StundenSchleifenThread;
import com.fx.sun.tools.SunChartAnchorPage;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shredzone.commons.suncalc.SunPosition;

/**
 *
 * @author pscha
 */
public class SunChartController implements Initializable, PopulateInterface {

    @FXML
    private BorderPane borderPane;
    @FXML
    private HBox hBox;
    @FXML
    private Button btnCalc;
    @FXML
    private TextField tfLat;
    @FXML
    private TextField tfLon;

    private LocalDate now = LocalDate.now();

    private static final Logger _log = LogManager.getLogger(SunChartController.class);
    private SunChartAnchorPage sunChartAnchorPage;
    private double lat;
    private double lon;

    private ResourceBundle bundle;

    public SunChartController() {
        lat = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LAT, Globals.DEFAULT_LOC.getLatitude() + ""));
        lon = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LON, Globals.DEFAULT_LOC.getLongitude() + ""));
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        this.bundle = bundle;

        hBox.getStyleClass().add("blue");

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        btnCalc.setText(bundle.getString("btn.calc.wgs"));

        sunChartAnchorPage = new SunChartAnchorPage();
        borderPane.setCenter(sunChartAnchorPage);

        btnCalc.setOnAction(e -> {
            lat = Double.valueOf(tfLat.getText());
            lon = Double.valueOf(tfLon.getText());
            setupAndRedraw();
        });

        tfLat.setOnKeyPressed(e -> {
            getClipboardFromGoogleMaps(e);
        });

        tfLon.setOnKeyPressed(e -> {
            getClipboardFromGoogleMaps(e);
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

    private void setupAndRedraw() {
        calculate();
        sunChartAnchorPage.redraw();
    }

    private void calculate() {

        sunChartAnchorPage.setLat(lat);
        sunChartAnchorPage.setLon(lon);
        sunChartAnchorPage.setMapSchleifen(calcSchleifen());
        sunChartAnchorPage.setMapSonnenStand(calcSonnenStandMap());
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

    @Override
    public void populate() {
        if (borderPane.getCenter() == null) {
            sunChartAnchorPage = new SunChartAnchorPage();
            borderPane.setCenter(sunChartAnchorPage);
        }

        lat = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LAT, Globals.DEFAULT_LOC.getLatitude() + ""));
        lon = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LON, Globals.DEFAULT_LOC.getLongitude() + ""));

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        setupAndRedraw();
    }

    @Override
    public void clear() {
        borderPane.setCenter(null);
    }
}
