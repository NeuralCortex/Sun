package com.fx.sun.controller;

import com.fx.sun.Globals;
import com.fx.sun.tools.InfoAnchorPage;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
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
public class DailyController implements Initializable, PopulateInterface {

    @FXML
    private BorderPane borderPane;
    @FXML
    private HBox hBox;
    @FXML
    private TextField tfLat;
    @FXML
    private TextField tfLon;
    @FXML
    private Label lbDate;
    @FXML
    private Button btnCalc;

    private double lat;
    private double lon;

    private InfoAnchorPage infoAnchorPage;

    private static final Logger _log = LogManager.getLogger(DailyController.class);
    private LocalDate now = LocalDate.now();

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        hBox.setId("hec-background-blue");
        lbDate.setId("hec-text-white");

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        btnCalc.setText(bundle.getString("btn.calc.wgs"));
        lbDate.setText(now.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", Locale.getDefault())));

        infoAnchorPage = new InfoAnchorPage();
        borderPane.setCenter(infoAnchorPage);

        btnCalc.setOnAction(e -> {
            lat = Double.valueOf(tfLat.getText());
            lon = Double.valueOf(tfLon.getText());
            calculate();
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

    private void calculate() {
        SunTimes.Parameters sunTimes = SunTimes.compute().on(now).at(lat, lon);
        infoAnchorPage.setSunTimes(sunTimes);
        infoAnchorPage.redraw();
    }

    @Override
    public void populate() {
        lat = Double.valueOf(Globals.propman.getProperty(Globals.COORD_LAT));
        lon = Double.valueOf(Globals.propman.getProperty(Globals.COORD_LON));

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

        SunTimes.Parameters sunTimes = SunTimes.compute().on(now).at(lat, lon);
        infoAnchorPage.setSunTimes(sunTimes);
        infoAnchorPage.redraw();
    }

    @Override
    public void clear() {

    }
}
