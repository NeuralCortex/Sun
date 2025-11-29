/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.sun.controller;

import com.fx.sun.controller.tabs.MoonController;
import com.fx.sun.controller.tabs.DistController;
import com.fx.sun.controller.tabs.BigDataController;
import com.fx.sun.controller.tabs.EleTimeController;
import com.fx.sun.controller.tabs.EleAziController;
import com.fx.sun.controller.tabs.GraphAnchorController;
import com.fx.sun.controller.tabs.MoonParamsController;
import com.fx.sun.controller.tabs.CalController;
import com.fx.sun.controller.tabs.SunParamsController;
import com.fx.sun.controller.tabs.SunChartPolarController;
import com.fx.sun.controller.tabs.DailyController;
import com.fx.sun.controller.tabs.SunChartController;
import com.fx.sun.controller.tabs.MoonTableController;
import com.fx.sun.Globals;
import com.fx.sun.dialog.OsmDialog;
import com.fx.sun.dialog.TestDialog;
import com.fx.sun.pojo.PosPOJO;
import com.fx.sun.tools.HelperFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author pscha
 */
public class MainController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private TabPane tabPane;
    @FXML
    private Label lbStatus;
    @FXML
    private HBox hboxStatus;
    @FXML
    private Label lbInfo;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuHelp;
    @FXML
    private MenuItem miSet;
    @FXML
    private MenuItem miOSM;
    @FXML
    private MenuItem miClose;
    @FXML
    private MenuItem miAbout;

    private static final Logger _log = LogManager.getLogger(MainController.class);
    private final Stage stage;

    public MainController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        borderPane.setPrefSize(Globals.WIDTH, Globals.HEIGHT);

        init(bundle);

        long start = System.currentTimeMillis();
        Tab tabDaily = addTab(bundle, Globals.FXML_INFO_PATH, new DailyController(), bundle.getString("tab.daily"));
        addTab(bundle, Globals.FXML_CAL_PATH, new CalController(), bundle.getString("tab.cal"));
        addTab(bundle, Globals.FXML_MOON_PATH, new MoonController(), bundle.getString("tab.moon"));
        addTab(bundle, Globals.FXML_MOON_PARAMS_PATH, new MoonParamsController(), bundle.getString("tab.moon.params"));
        addTab(bundle, Globals.FXML_SUN_PARAMS_PATH, new SunParamsController(), bundle.getString("tab.sun.params"));
        addTab(bundle, Globals.FXML_MOON_TABLE_PATH, new MoonTableController(this), bundle.getString("tab.sun.moon"));
        addTab(bundle, Globals.FXML_BIG_DATA_PATH, new BigDataController(this), bundle.getString("tab.big"));
        addTab(bundle, Globals.FXML_GRAPH_ANCHOR_PATH, new GraphAnchorController(), bundle.getString("tab.sunrise.sunset"));
        addTab(bundle, Globals.FXML_ELE_TIME_PATH, new EleTimeController(), bundle.getString("tab.ele.time"));
        addTab(bundle, Globals.FXML_ELE_AZI_PATH, new EleAziController(), bundle.getString("tab.ele.azi"));
        addTab(bundle, Globals.FXML_DISTANCE_PATH, new DistController(DistController.TYPE.MOON), bundle.getString("tab.dist.moon"));
        addTab(bundle, Globals.FXML_DISTANCE_PATH, new DistController(DistController.TYPE.SUN), bundle.getString("tab.dist.sun"));
        Tab tabSunChart = addTab(bundle, Globals.FXML_SUN_CHART_PATH, new SunChartController(), bundle.getString("tab.sun.chart"));
        Tab tabSunPolarChart = addTab(bundle, Globals.FXML_SUN_CHART_POLAR_PATH, new SunChartPolarController(), bundle.getString("tab.sun.chart.polar"));
        long end = System.currentTimeMillis();
        System.out.println("Full load time: " + (end - start) + "ms");

        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
            ((PopulateInterface) newValue.getContent().getUserData()).populate();
            lbStatus.setText("");

            if (newValue != tabSunChart || newValue != tabSunPolarChart) {
                ((PopulateInterface) tabSunChart.getContent().getUserData()).clear();
                ((PopulateInterface) tabSunPolarChart.getContent().getUserData()).clear();
                System.out.println("Clearing Tab Sun Chart and Tab Sun Polar");
            }
            if (newValue == tabSunChart) {
                ((PopulateInterface) tabSunChart.getContent().getUserData()).populate();
                System.out.println("Restore Tab Sun Chart");

            }
            if (newValue == tabSunPolarChart) {
                ((PopulateInterface) tabSunPolarChart.getContent().getUserData()).populate();
                System.out.println("Restore Tab Sun Chart Polar");
            }
        });

        miOSM.setOnAction(e -> {
            OsmDialog osmDialog = new OsmDialog(bundle);
            HelperFunctions.styleDialogButtons(osmDialog);
            Optional<PosPOJO> res = osmDialog.showAndWait();
            if (res.isPresent()) {
                PosPOJO posPOJO = res.get();
                Globals.propman.setProperty(Globals.COORD_LAT, posPOJO.getLat() + "");
                Globals.propman.setProperty(Globals.COORD_LON, posPOJO.getLon() + "");
                Globals.propman.save();

                refreshCoords();
            }
        });

        miSet.setOnAction(e -> {
            TestDialog dialog = new TestDialog(bundle);
            HelperFunctions.styleDialogButtons(dialog);
            Optional<PosPOJO> res = dialog.showAndWait();
            if (res.isPresent()) {
                PosPOJO posPOJO = res.get();
                Globals.propman.setProperty(Globals.COORD_LAT, posPOJO.getLat() + "");
                Globals.propman.setProperty(Globals.COORD_LON, posPOJO.getLon() + "");
                Globals.propman.save();

                refreshCoords();
            }
        });

        miClose.setOnAction(e -> {
            System.exit(0);
        });

        miAbout.setOnAction(e -> {
            showAboutDlg(bundle);
        });

        ((PopulateInterface) tabDaily.getContent().getUserData()).populate();
    }

    private void refreshCoords() {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.isSelected()) {
                ((PopulateInterface) tab.getContent().getUserData()).populate();
            }
        }
    }

    private Tab addTab(ResourceBundle bundle, String path, Object controller, String tabName) {
        long start = System.currentTimeMillis();
        Tab tab = new Tab(tabName);
        tabPane.getTabs().add(tab);
        HelperFunctions helperFunctions = new HelperFunctions();
        Node node = helperFunctions.loadFxml(bundle, path, controller);
        node.setUserData(controller);
        tab.setContent(node);
        long end = System.currentTimeMillis();
        System.out.println("Loadtime (" + controller.toString() + ") in ms: " + (end - start));
        return tab;
    }

    private void showAboutDlg(ResourceBundle bundle) {
        Alert alert = new Alert(AlertType.INFORMATION);
        HelperFunctions.centerWindow(alert.getDialogPane().getScene().getWindow());

        Stage stageDlg = (Stage) alert.getDialogPane().getScene().getWindow();
        alert.getDialogPane().getStylesheets().add(Globals.CSS_PATH);
        try {
            stageDlg.getIcons().add(new Image(new FileInputStream(new File(Globals.APP_LOGO_PATH))));
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }

        alert.setTitle(bundle.getString("dlg.about.info"));
        alert.setHeaderText(bundle.getString("dlg.about.header"));
        String programmer = bundle.getString("dlg.about.content");
        alert.setContentText(MessageFormat.format(programmer, LocalDate.now().getYear()));

        alert.showAndWait();
    }

    private void init(ResourceBundle bundle) {
        hboxStatus.getStyleClass().add("blue");

        menuFile.setText(bundle.getString("menu.file"));
        menuHelp.setText(bundle.getString("menu.help"));

        miAbout.setText(bundle.getString("mi.about"));
        miClose.setText(bundle.getString("mi.close"));
        miSet.setText(bundle.getString("mi.set.coord"));
        miOSM.setText(bundle.getString("mi.set.coord.osm"));

        String programmer = bundle.getString("dlg.about.content");
        lbInfo.setText(MessageFormat.format(programmer, LocalDate.now().getYear()));
    }

    public Stage getStage() {
        return stage;
    }

    public Label getLbStatus() {
        return lbStatus;
    }

    public Label getLbInfo() {
        return lbInfo;
    }
}
