package com.fx.sun.controller;

import com.fx.sun.Globals;
import com.fx.sun.controller.cell.SunParamsCell;
import com.fx.sun.controller.cell.KwCell;
import com.fx.sun.pojo.DatePOJO;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author pscha
 */
public class SunParamsController implements Initializable, PopulateInterface {

    @FXML
    private TableView<DatePOJO> table;
    @FXML
    private Button btnYearUp;
    @FXML
    private Button btnYearDown;
    @FXML
    private Button btnMonthUp;
    @FXML
    private Button btnMonthDown;
    @FXML
    private Label lbYear;
    @FXML
    private Label lbMonth;
    @FXML
    private HBox hBox;
    @FXML
    private TextField tfLat;
    @FXML
    private TextField tfLon;
    @FXML
    private TextField tfTime;
    @FXML
    private Button btnCalc;

    private LocalDate monday = null;
    private LocalDate tuesday = null;
    private LocalDate wednesday = null;
    private LocalDate thursday = null;
    private LocalDate friday = null;
    private LocalDate saturday = null;
    private LocalDate sunday = null;

    private TableColumn colKW = null;
    private TableColumn colMon = null;
    private TableColumn colDie = null;
    private TableColumn colMit = null;
    private TableColumn colDon = null;
    private TableColumn colFri = null;
    private TableColumn colSat = null;
    private TableColumn colSun = null;

    private LocalDate now = LocalDate.now();

    private List<DatePOJO> list = new ArrayList<>();

    private double lat;
    private double lon;

    private int hour;
    private int min;
    private int sec;

    private static final Logger _log = LogManager.getLogger(SunParamsController.class);
    private ResourceBundle bundle;

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        this.bundle = bundle;

        hBox.setId("hec-background-blue");
        lbYear.setId("hec-text-white");
        lbMonth.setId("hec-text-white");

        btnYearUp.setText("<");
        btnYearDown.setText(">");

        btnMonthUp.setText("<");
        btnMonthDown.setText(">");

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");
        tfTime.setText("12:00:00");

        btnCalc.setText(bundle.getString("btn.calc.wgs"));

        colKW = new TableColumn(bundle.getString("col.kw"));
        colMon = new TableColumn(bundle.getString("col.mon"));
        colDie = new TableColumn(bundle.getString("col.tue"));
        colMit = new TableColumn(bundle.getString("col.wed"));
        colDon = new TableColumn(bundle.getString("col.thu"));
        colFri = new TableColumn(bundle.getString("col.fri"));
        colSat = new TableColumn(bundle.getString("col.sat"));
        colSun = new TableColumn(bundle.getString("col.sun"));

        colKW.setCellValueFactory(new PropertyValueFactory("monday"));
        colMon.setCellValueFactory(new PropertyValueFactory("monday"));
        colDie.setCellValueFactory(new PropertyValueFactory("tuesday"));
        colMit.setCellValueFactory(new PropertyValueFactory("wednesday"));
        colDon.setCellValueFactory(new PropertyValueFactory("thursday"));
        colFri.setCellValueFactory(new PropertyValueFactory("friday"));
        colSat.setCellValueFactory(new PropertyValueFactory("saturday"));
        colSun.setCellValueFactory(new PropertyValueFactory("sunday"));

        table.getColumns().addAll(colKW, colMon, colDie, colMit, colDon, colFri, colSat, colSun);
        table.getSelectionModel().setCellSelectionEnabled(true);

        lbYear.setText(now.getYear() + "");
        lbMonth.setText(now.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())));

        btnYearUp.setOnAction(e -> {
            now = now.minusYears(1);
            lbYear.setText(now.getYear() + "");
            populateTable();
        });

        btnYearDown.setOnAction(e -> {
            now = now.plusYears(1);
            lbYear.setText(now.getYear() + "");
            populateTable();
        });

        btnMonthUp.setOnAction(e -> {
            now = now.minusMonths(1);
            int oldYear = Integer.valueOf(lbYear.getText());
            if (now.getYear() != oldYear) {
                lbYear.setText((oldYear - 1) + "");
            }
            lbMonth.setText(now.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())));
            populateTable();
        });

        btnMonthDown.setOnAction(e -> {
            now = now.plusMonths(1);
            int oldYear = Integer.valueOf(lbYear.getText());
            if (now.getYear() != oldYear) {
                lbYear.setText((oldYear + 1) + "");
            }
            lbMonth.setText(now.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())));
            populateTable();
        });

        btnCalc.setOnAction(e -> {
            lat = Double.valueOf(tfLat.getText());
            lon = Double.valueOf(tfLon.getText());
            String timeStr[] = tfTime.getText().split(":");
            hour = Integer.valueOf(timeStr[0]);
            min = Integer.valueOf(timeStr[1]);
            sec = Integer.valueOf(timeStr[2]);
            populateTable();
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

    private void populateTable() {

        colKW.setCellFactory((param) -> {
            return new KwCell(bundle);
        });
        colMon.setCellFactory((param) -> {
            return new SunParamsCell(lat, lon, now, hour, min, sec, bundle);
        });
        colDie.setCellFactory((param) -> {
            return new SunParamsCell(lat, lon, now, hour, min, sec, bundle);
        });
        colMit.setCellFactory((param) -> {
            return new SunParamsCell(lat, lon, now, hour, min, sec, bundle);
        });
        colDon.setCellFactory((param) -> {
            return new SunParamsCell(lat, lon, now, hour, min, sec, bundle);
        });
        colFri.setCellFactory((param) -> {
            return new SunParamsCell(lat, lon, now, hour, min, sec, bundle);
        });
        colSat.setCellFactory((param) -> {
            return new SunParamsCell(lat, lon, now, hour, min, sec, bundle);
        });
        colSun.setCellFactory((param) -> {
            return new SunParamsCell(lat, lon, now, hour, min, sec, bundle);
        });

        int year = now.getYear();
        int month = now.getMonth().getValue();

        list = new ArrayList<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate start = startDate.with(DayOfWeek.MONDAY);
        LocalDate endDate = LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth());
        LocalDate end = endDate.with(DayOfWeek.SUNDAY);

        Stream<LocalDate> dates = start.datesUntil(end.plusDays(1));
        dates.forEach((day) -> {
            if (day.getDayOfWeek() == DayOfWeek.MONDAY) {
                monday = day;
            }
            if (day.getDayOfWeek() == DayOfWeek.TUESDAY) {
                tuesday = day;

            }
            if (day.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                wednesday = day;
            }
            if (day.getDayOfWeek() == DayOfWeek.THURSDAY) {
                thursday = day;
            }
            if (day.getDayOfWeek() == DayOfWeek.FRIDAY) {
                friday = day;
            }
            if (day.getDayOfWeek() == DayOfWeek.SATURDAY) {
                saturday = day;
            }
            if (day.getDayOfWeek() == DayOfWeek.SUNDAY) {
                sunday = day;
                list.add(new DatePOJO(monday, tuesday, wednesday, thursday, friday, saturday, sunday));
            }
        });

        table.getItems().clear();
        table.setItems(FXCollections.observableArrayList(list));
    }

    @Override
    public void populate() {
        lat = Double.valueOf(Globals.propman.getProperty(Globals.COORD_LAT));
        lon = Double.valueOf(Globals.propman.getProperty(Globals.COORD_LON));

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        populateTable();
    }

    @Override
    public void clear() {

    }
}
