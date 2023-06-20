package com.fx.sun.controller;

import com.fx.sun.controller.cell.MoonCell;
import com.fx.sun.controller.cell.KwCell;
import com.fx.sun.Globals;
import com.fx.sun.pojo.DatePOJO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
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
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javax.imageio.ImageIO;

/**
 *
 * @author pscha
 */
public class MoonController implements Initializable, PopulateInterface {

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
    private CheckBox cbNight;

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

    private ResourceBundle bundle;
    private Image moon = null;
    private int size = 40;

    public MoonController() {
        try {
            BufferedImage bi = ImageIO.read(new File(Globals.MOON_IMAGE_PATH));
            bi = convertToBufferedImage(resizeImage(bi, size, size));
            moon = SwingFXUtils.toFXImage(bi, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private java.awt.Image resizeImage(BufferedImage bufferedImage, int width, int height) {
        return bufferedImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
    }

    public static BufferedImage convertToBufferedImage(java.awt.Image image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

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
        
        cbNight.setText("Night Vision");
        cbNight.setId("hec-text-white");

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
        
        cbNight.selectedProperty().addListener((ov,o,n)->{
            populateTable();
        });
    }

    private void populateTable() {
        
        boolean isNight=cbNight.isSelected();

        colKW.setCellFactory((param) -> {
            return new KwCell(bundle);
        });
        colMon.setCellFactory((param) -> {
            return new MoonCell(now, moon, bundle,isNight);
        });
        colDie.setCellFactory((param) -> {
            return new MoonCell(now, moon, bundle,isNight);
        });
        colMit.setCellFactory((param) -> {
            return new MoonCell(now, moon, bundle,isNight);
        });
        colDon.setCellFactory((param) -> {
            return new MoonCell(now, moon, bundle,isNight);
        });
        colFri.setCellFactory((param) -> {
            return new MoonCell(now, moon, bundle,isNight);
        });
        colSat.setCellFactory((param) -> {
            return new MoonCell(now, moon, bundle,isNight);
        });
        colSun.setCellFactory((param) -> {
            return new MoonCell(now, moon, bundle,isNight);
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
        populateTable();
    }

    @Override
    public void clear() {

    }
}
