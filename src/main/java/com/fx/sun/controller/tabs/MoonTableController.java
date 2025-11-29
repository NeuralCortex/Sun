package com.fx.sun.controller.tabs;

import com.fx.sun.Globals;
import com.fx.sun.controller.MainController;
import com.fx.sun.controller.PopulateInterface;
import com.fx.sun.controller.cell.VisibleCell;
import com.fx.sun.pojo.ComboBoxPOJO;
import com.fx.sun.pojo.TablePOJO;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shredzone.commons.suncalc.MoonPosition;
import org.shredzone.commons.suncalc.SunPosition;

/**
 *
 * @author pscha
 */
public class MoonTableController implements Initializable, PopulateInterface {

    @FXML
    private HBox hBox;
    @FXML
    private TextField tfLat;
    @FXML
    private TextField tfLon;
    @FXML
    private Button btnCalc;
    @FXML
    private Button btnCSV;
    @FXML
    private RadioButton rbMoon;
    @FXML
    private RadioButton rbSun;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<ComboBoxPOJO> cbMin;
    @FXML
    private TableView<TablePOJO> table;

    private double lat;
    private double lon;
    private int min = 60;

    private TableColumn colDate;
    private TableColumn colTime;
    private TableColumn colAzi;
    private TableColumn colAlti;
    private TableColumn colDist;
    private TableColumn colPara;
    private TableColumn colTrueAlti;

    private List<TablePOJO> list = new ArrayList<>();

    private static final Logger _log = LogManager.getLogger(MoonParamsController.class);
    private LocalDate now = LocalDate.now();
    private final MainController mainController;
    private ResourceBundle bundle;

    public MoonTableController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        this.bundle = bundle;

        hBox.getStyleClass().add("blue");

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");
        datePicker.setValue(now);

        btnCalc.setText(bundle.getString("btn.calc.wgs"));
        btnCSV.setText(bundle.getString("btn.csv"));
        rbMoon.setText(bundle.getString("moon"));
        rbSun.setText(bundle.getString("sun"));

        ToggleGroup toggleGroup = new ToggleGroup();
        rbMoon.setToggleGroup(toggleGroup);
        rbSun.setToggleGroup(toggleGroup);
        rbMoon.setSelected(true);

        ArrayList cbList = new ArrayList();
        cbList.add(new ComboBoxPOJO(60, "60 min"));
        cbList.add(new ComboBoxPOJO(30, "30 min"));
        cbList.add(new ComboBoxPOJO(10, "10 min"));
        cbList.add(new ComboBoxPOJO(1, "1 min"));

        cbMin.getItems().addAll(cbList);
        cbMin.getSelectionModel().selectFirst();

        colDate = new TableColumn<>(bundle.getString("date"));
        colTime = new TableColumn<>(bundle.getString("time"));
        colAzi = new TableColumn(bundle.getString("azi") + " in °");
        colAlti = new TableColumn(bundle.getString("alti") + " in °");
        colDist = new TableColumn(bundle.getString("dist") + " in km");
        colPara = new TableColumn(bundle.getString("para") + " in °");
        colTrueAlti = new TableColumn(bundle.getString("truealti") + " in °");

        colDate.setCellValueFactory(new PropertyValueFactory("date"));
        colTime.setCellValueFactory(new PropertyValueFactory("time"));
        colAzi.setCellValueFactory(new PropertyValueFactory("azimuth"));
        colAlti.setCellValueFactory(new PropertyValueFactory("altitude"));
        colDist.setCellValueFactory(new PropertyValueFactory("distance"));
        colPara.setCellValueFactory(new PropertyValueFactory("parallactic"));
        colTrueAlti.setCellValueFactory(new PropertyValueFactory("truealti"));

        colAlti.setCellFactory((param) -> {
            return new VisibleCell();
        });

        colPara.setCellFactory((param) -> {
            return new VisibleCell();
        });

        colTrueAlti.setCellFactory((param) -> {
            return new VisibleCell();
        });

        table.getColumns().addAll(colDate, colTime, colAzi, colAlti, colDist, colPara, colTrueAlti);

        btnCalc.setOnAction(e -> {
            lat = Double.valueOf(tfLat.getText());
            lon = Double.valueOf(tfLon.getText());

            populateTable();
        });

        btnCSV.setOnAction(e -> {
            exportCSV();
        });

        datePicker.valueProperty().addListener((ov, o, n) -> {
            populateTable();
        });

        cbMin.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            populateTable();
        });

        rbMoon.selectedProperty().addListener((ov, o, n) -> {
            populateTable();
        });

        rbSun.selectedProperty().addListener((ov, o, n) -> {
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

        now = datePicker.getValue();
        min = cbMin.getSelectionModel().getSelectedItem().getId();

        list = new ArrayList<>();
        table.getItems().clear();

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < (60 / min); j++) {
                Date date = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd.MM.yyyy");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                DecimalFormat decimalFormat = new DecimalFormat("#########.################");

                LocalDateTime t = now.atTime(i, j * min, 0);

                TablePOJO tablePOJO = null;

                if (rbMoon.isSelected()) {
                    MoonPosition moonPosition = MoonPosition.compute().on(t).at(lat, lon).execute();

                    String dateNow = simpleDateFormat.format(calendar.getTime());
                    String time = formatter.format(t);
                    double azi = moonPosition.getAzimuth();
                    double alti = moonPosition.getAltitude();
                    String dist = moonPosition.getDistance() + "";
                    Double para = moonPosition.getParallacticAngle();

                    tablePOJO = new TablePOJO(dateNow, time, azi, alti, dist, para, null);
                    if (rbSun.isSelected()) {
                        System.out.println("para" + para);
                    }
                } else if (rbSun.isSelected()) {
                    SunPosition sunPosition = SunPosition.compute().on(t).at(lat, lon).execute();

                    String dateNow = simpleDateFormat.format(calendar.getTime());
                    String time = formatter.format(t);
                    double azi = sunPosition.getAzimuth();
                    double alti = sunPosition.getAltitude();
                    String dist = decimalFormat.format(sunPosition.getDistance());
                    double trueAlti = sunPosition.getTrueAltitude();

                    tablePOJO = new TablePOJO(dateNow, time, azi, alti, dist, null, trueAlti);
                }
                list.add(tablePOJO);
            }
        }
        table.setItems(FXCollections.observableArrayList(list));
    }

    private void exportCSV() {

        Date date = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String day = simpleDateFormat.format(calendar.getTime());
        String obj = rbMoon.isSelected() ? bundle.getString("moon") : bundle.getString("sun");
        String res = "Time Res " + cbMin.getSelectionModel().getSelectedItem().getName() + "";

        FileChooser fileChooser = new FileChooser();
        String fileName = obj + " " + day + " " + res + ".csv";
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV-Files", "*.csv"));
        fileChooser.setInitialDirectory(new File(Globals.propman.getProperty(Globals.CSV_DIR, System.getProperty("user.dir"))));
        File file = fileChooser.showSaveDialog(mainController.getStage());
        if (file != null) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                String header = bundle.getString("date") + ";"
                        + bundle.getString("time") + ";"
                        + bundle.getString("azi") + ";"
                        + bundle.getString("alti") + ";"
                        + bundle.getString("dist") + ";"
                        + bundle.getString("para") + ";"
                        + bundle.getString("truealti");
                bw.write(header);
                bw.newLine();
                for (TablePOJO tablePOJO : table.getItems()) {
                    String line = tablePOJO.getDate() + ";"
                            + tablePOJO.getTime() + ";"
                            + tablePOJO.getAzimuth() + ";"
                            + tablePOJO.getAltitude() + ";"
                            + tablePOJO.getDistance() + ";"
                            + tablePOJO.getParallactic() + ";"
                            + tablePOJO.getTruealti();
                    bw.write(line);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            } catch (Exception ex) {
                _log.error(ex.getMessage());
            }
            Globals.propman.setProperty(Globals.CSV_DIR, file.getParent());
            Globals.propman.save();
        }
    }

    @Override
    public void populate() {
         lat = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LAT, Globals.DEFAULT_LOC.getLatitude() + ""));
        lon = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LON, Globals.DEFAULT_LOC.getLongitude() + ""));

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        populateTable();
    }

    @Override
    public void clear() {

    }

}
