package com.fx.sun.controller.tabs;

import com.fx.sun.Globals;
import com.fx.sun.controller.PopulateInterface;
import com.fx.sun.pojo.DatePosPOJO;
import com.fx.sun.pojo.TimePOJO;
import com.fx.sun.tools.DistAnchorPane;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shredzone.commons.suncalc.MoonPosition;
import org.shredzone.commons.suncalc.SunPosition;

/**
 *
 * @author pscha
 */
public class DistController implements Initializable, PopulateInterface {

    @FXML
    private BorderPane borderPane;
    @FXML
    private HBox hBox;
    @FXML
    private Button btnYearDown;
    @FXML
    private Button btnYearUp;
    @FXML
    private Button btnCalc;
    @FXML
    private Label lbYear;
    @FXML
    private TextField tfLat;
    @FXML
    private TextField tfLon;

    private LocalDate now = LocalDate.now();

    private static final Logger _log = LogManager.getLogger(DistController.class);
    DistAnchorPane distAnchorPane;
    private double lat;
    private double lon;

    public static enum TYPE {
        SUN, MOON
    };
    private final TYPE type;

    private ResourceBundle bundle;

    public DistController(TYPE type) {
        this.type = type;
         lat = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LAT, Globals.DEFAULT_LOC.getLatitude() + ""));
        lon = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LON, Globals.DEFAULT_LOC.getLongitude() + ""));
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        this.bundle = bundle;

        hBox.getStyleClass().add("blue");

        btnYearDown.setText("<");
        btnYearUp.setText(">");
        lbYear.setText(now.getYear() + "");

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        btnCalc.setText(bundle.getString("btn.calc.wgs"));

        distAnchorPane = new DistAnchorPane(type);
        setupAndRedraw();
        borderPane.setCenter(distAnchorPane);

        distAnchorPane.setOnMouseMoved(e -> {
            distAnchorPane.setMx(e.getX());
            distAnchorPane.setMy(e.getY());
            distAnchorPane.redraw();
        });

        btnYearUp.setOnAction(e -> {
            now = now.plusYears(1);
            lbYear.setText(now.getYear() + "");
            setupAndRedraw();
        });

        btnYearDown.setOnAction(e -> {
            now = now.minusYears(1);
            lbYear.setText(now.getYear() + "");
            setupAndRedraw();
        });

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
        distAnchorPane.setNow(now);
        distAnchorPane.setDaysInYear(now.lengthOfYear());
        distAnchorPane.setListMonths(calcMonths(now));
        distAnchorPane.setLat(lat);
        distAnchorPane.setLon(lon);
        passGraphToAnchorPane();
        distAnchorPane.redraw();
    }

    private List<DatePosPOJO> calcMonths(LocalDate date) {
        LocalDate yearStart = LocalDate.ofYearDay(date.getYear(), 1);
        int j = 0;
        List<DatePosPOJO> list = new ArrayList<>();
        for (int k = 0; k <= 12; k++) {
            LocalDate m = yearStart.plusMonths(k);
            list.add(new DatePosPOJO(j, m));
            j += m.lengthOfMonth();
        }
        return list;
    }

    private void passGraphToAnchorPane() {
        LocalDate yearStart = LocalDate.ofYearDay(now.getYear(), 1);
        LocalDate yearEnd = LocalDate.ofYearDay(now.getYear(), now.lengthOfYear());

        Stream<LocalDate> dates = yearStart.datesUntil(yearEnd.plusDays(1));
        List<TimePOJO> list = getDist(dates);

        double min = list.stream().min(Comparator.comparing(t -> t.getTime())).get().getTime();
        double max = list.stream().max(Comparator.comparing(t -> t.getTime())).get().getTime();

        distAnchorPane.setListDist(list, min, max);
    }

    private List<TimePOJO> getDist(Stream<LocalDate> stream) {
        return stream.
                map(m -> new TimePOJO(m.getDayOfYear(), getDist(m))).
                collect(Collectors.toList());
    }

    private double getDist(LocalDate date) {
        double distance;
        if (type == TYPE.MOON) {
            MoonPosition moonPosition = MoonPosition.compute().on(date).at(lat, lon).execute();
            distance = moonPosition.getDistance();
        } else {
            SunPosition sunPosition = SunPosition.compute().on(date).at(lat, lon).execute();
            distance = sunPosition.getDistance();
        }
        return distance;
    }

    @Override
    public void populate() {
         lat = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LAT, Globals.DEFAULT_LOC.getLatitude() + ""));
        lon = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LON, Globals.DEFAULT_LOC.getLongitude() + ""));

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        setupAndRedraw();
    }

    @Override
    public void clear() {

    }
}
