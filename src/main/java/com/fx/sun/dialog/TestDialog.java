package com.fx.sun.dialog;

import com.fx.sun.Globals;
import com.fx.sun.pojo.PosPOJO;
import com.fx.sun.tools.HelperFunctions;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author pscha
 */
public class TestDialog extends Dialog<PosPOJO> implements Initializable {

    @FXML
    private Label lbLat;
    @FXML
    private Label lbLon;
    @FXML
    private TextField tfLat;
    @FXML
    private TextField tfLon;

    private static final Logger _log = LogManager.getLogger(TestDialog.class);

    private double lat;
    private double lon;

    private final ResourceBundle bundle;

    public TestDialog(ResourceBundle bundle) {
        this.bundle = bundle;
        init();
    }

    private void init() {
        HelperFunctions.centerWindow(getDialogPane().getScene().getWindow());

        Stage stageDlg = (Stage) getDialogPane().getScene().getWindow();
        getDialogPane().getStylesheets().add(Globals.CSS_PATH);
        try {
            stageDlg.getIcons().add(new Image(new FileInputStream(new File(Globals.APP_LOGO_PATH))));
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }

        setTitle(bundle.getString("dlg.wgs.title"));

        ButtonType saveButtonType = new ButtonType(bundle.getString("dlg.wgs.btn.save"), ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Node node = loadDialog(bundle, Globals.DLG_WGS_PATH, this);
        getDialogPane().setContent(node);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    lat = Double.parseDouble(tfLat.getText());
                    lon = Double.parseDouble(tfLon.getText());
                } catch (NumberFormatException ex) {
                    _log.error(ex.getMessage());
                }
                return new PosPOJO(lat, lon);
            }
            return null;
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        lbLat.setText(bundle.getString("dlg.wgs.lat") + " (-90° - 90°)");
        lbLon.setText(bundle.getString("dlg.wgs.lon") + " (-180° - 180°)");

        lat = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LAT, Globals.DEFAULT_LOC.getLatitude() + ""));
        lon = Double.parseDouble(Globals.propman.getProperty(Globals.COORD_LON, Globals.DEFAULT_LOC.getLongitude() + ""));

        tfLat.setText(lat + "");
        tfLon.setText(lon + "");

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

    private Node loadDialog(ResourceBundle bundle, String path, Object controller) {
        HelperFunctions helperFunctions = new HelperFunctions();
        Node node = helperFunctions.loadFxml(bundle, path, controller);
        node.setUserData(controller);
        return node;
    }
}
