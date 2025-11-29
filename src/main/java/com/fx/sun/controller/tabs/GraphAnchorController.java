package com.fx.sun.controller.tabs;

import com.fx.sun.Globals;
import com.fx.sun.controller.PopulateInterface;
import com.fx.sun.pojo.DatePosPOJO;
import com.fx.sun.pojo.TimePOJO;
import com.fx.sun.tools.SunriseAnchorPane;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import org.shredzone.commons.suncalc.SunTimes;

/**
 *
 * @author pscha
 */
public class GraphAnchorController implements Initializable, PopulateInterface {

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

    private static final Logger _log = LogManager.getLogger(GraphAnchorController.class);
    SunriseAnchorPane sunriseAnchorPane;
    private double lat;
    private double lon;

    public static enum TYPE {
        SUNSET, SUNRISE
    };

    private ResourceBundle bundle;

    public GraphAnchorController() {
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

        sunriseAnchorPane = new SunriseAnchorPane();
        borderPane.setCenter(sunriseAnchorPane);

        sunriseAnchorPane.setOnMouseMoved(e -> {
            sunriseAnchorPane.setMx(e.getX());
            sunriseAnchorPane.setMy(e.getY());
            sunriseAnchorPane.redraw();
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
        sunriseAnchorPane.setNow(now);
        sunriseAnchorPane.setDaysInYear(now.lengthOfYear());
        sunriseAnchorPane.setListMonths(calcMonths(now));
        sunriseAnchorPane.setLat(lat);
        sunriseAnchorPane.setLon(lon);
        passGraphToAnchorPane();
        sunriseAnchorPane.redraw();
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

        Stream<LocalDate> datesRise = yearStart.datesUntil(yearEnd.plusDays(1));
        Stream<LocalDate> datesSunset = yearStart.datesUntil(yearEnd.plusDays(1));
        List<TimePOJO> listRise = filter99FromStream(datesRise, GraphAnchorController.TYPE.SUNRISE);
        List<TimePOJO> listSet = filter99FromStream(datesSunset, GraphAnchorController.TYPE.SUNSET);

        sunriseAnchorPane.setListRise(listRise);
        sunriseAnchorPane.setListSunset(listSet);
    }

    private List<TimePOJO> filter99FromStream(Stream<LocalDate> stream, GraphAnchorController.TYPE type) {
        return stream.
                filter(f -> !getTime(f, type).contains("99")).
                map(m -> new TimePOJO(m.getDayOfYear(), getTimeDouble(m, type))).
                collect(Collectors.toList());
    }

    private double getTimeDouble(LocalDate date, GraphAnchorController.TYPE type) {
        String time = getTime(date, type);

        String sun[] = time.split(":");
        int sunHour = Integer.valueOf(sun[0]);
        int sunMinutes = Integer.valueOf(sun[1]);

        double sunTime = sunHour + sunMinutes / (60.0f);
        return sunTime;
    }

    private String getTime(LocalDate date, GraphAnchorController.TYPE type) {
        Date datum = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datum);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Globals.DEFAULT_LOCALE);

        SunTimes zm = SunTimes.compute().on(date).at(lat, lon).execute();

        String sunset;
        if (type == GraphAnchorController.TYPE.SUNSET) {
            //sunset = calculator.getOfficialSunsetForDate(calendar);
            sunset = zm.getRise() != null ? zm.getRise().format(dateTimeFormatter) : "99:99";
            if (zm.isAlwaysUp() || zm.isAlwaysDown()) {
                sunset = "99:99";
            }
        } else {
            //sunset = calculator.getOfficialSunriseForDate(calendar);
            sunset = zm.getSet() != null ? zm.getSet().format(dateTimeFormatter) : "99:99";
            if (zm.isAlwaysUp() || zm.isAlwaysDown()) {
                sunset = "99:99";
            }
        }

        return sunset;
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
