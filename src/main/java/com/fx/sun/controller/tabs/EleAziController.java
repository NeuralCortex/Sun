package com.fx.sun.controller.tabs;

import com.fx.sun.Globals;
import com.fx.sun.controller.PopulateInterface;
import com.fx.sun.pojo.EleTimePOJO;
import com.fx.sun.tools.EleAziAnchorPage;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
import org.shredzone.commons.suncalc.MoonPosition;
import org.shredzone.commons.suncalc.SunPosition;

/**
 *
 * @author pscha
 */
public class EleAziController implements Initializable, PopulateInterface {
    
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
    @FXML
    private DatePicker datePicker;
    
    private LocalDate now = LocalDate.now();
    
    private static final Logger _log = LogManager.getLogger(EleAziController.class);
    EleAziAnchorPage eleAziAnchorPage;
    private double lat;
    private double lon;
    
    private ResourceBundle bundle;
    
    public EleAziController() {
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
        datePicker.setValue(now);
        
        eleAziAnchorPage = new EleAziAnchorPage();
        borderPane.setCenter(eleAziAnchorPage);
        
        btnCalc.setOnAction(e -> {
            lat = Double.valueOf(tfLat.getText());
            lon = Double.valueOf(tfLon.getText());
            setupAndRedraw();
        });
        
        datePicker.valueProperty().addListener((ov,o,n)->{
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
        eleAziAnchorPage.redraw();
    }
    
    private void calculate() {
        now = datePicker.getValue();
        
        List<EleTimePOJO> listSun = new ArrayList<>();
        List<EleTimePOJO> listMoon = new ArrayList<>();
        
        int min = 1;
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < (60 / min); j++) {
                LocalDateTime t = now.atTime(i, j * min, 0);
                
                double time = i + (j / 60.0f);
                
                SunPosition sunPosition = SunPosition.compute().on(t).at(lat, lon).execute();
                double azi = sunPosition.getAzimuth();
                double alti = sunPosition.getAltitude();
                
                listSun.add(new EleTimePOJO(time, azi, alti,t));
                
                MoonPosition moonPosition = MoonPosition.compute().on(t).at(lat, lon).execute();
                azi = moonPosition.getAzimuth();
                alti = moonPosition.getAltitude();
                
                listMoon.add(new EleTimePOJO(time, azi, alti,t));
            }
        }
        
        listSun.sort(Comparator.comparing(t->t.getAzimuth()));
        listMoon.sort(Comparator.comparing(t->t.getAzimuth()));
        
        double maxSun = listSun.stream().max(Comparator.comparing(t -> t.getAltitude())).get().getAltitude();
        double maxMoon = listMoon.stream().max(Comparator.comparing(t -> t.getAltitude())).get().getAltitude();
        
        eleAziAnchorPage.setListSun(listSun);
        eleAziAnchorPage.setListMoon(listMoon);
        eleAziAnchorPage.setMaxSun(maxSun);
        eleAziAnchorPage.setMaxMoon(maxMoon);
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
