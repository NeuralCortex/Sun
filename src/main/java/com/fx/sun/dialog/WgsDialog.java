package com.fx.sun.dialog;

import com.fx.sun.Globals;
import com.fx.sun.pojo.PosPOJO;
import com.fx.sun.tools.HelperFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author pscha
 */
public class WgsDialog extends Dialog<PosPOJO> {

    private static final Logger _log = LogManager.getLogger(WgsDialog.class);
    //Kriegerehrenmal Zella-Mehlis
    private double lat = 50.659338995337976;
    private double lon = 10.665138248049024;

    private final ResourceBundle bundle;

    public WgsDialog(ResourceBundle bundle) {
        this.bundle = bundle;
        init();
    }

    private void init() {
        HelperFunctions.centerWindow(getDialogPane().getScene().getWindow());

        Stage stageDlg = (Stage) getDialogPane().getScene().getWindow();
        try {
            stageDlg.getIcons().add(new Image(new FileInputStream(new File(Globals.APP_LOGO_PATH))));
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }

        setTitle(bundle.getString("dlg.wgs.title"));

        ButtonType saveButtonType = new ButtonType(bundle.getString("dlg.wgs.btn.save"), ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Label lbLat = new Label(bundle.getString("dlg.wgs.lat") + " (-90° - 90°)");
        Label lbLon = new Label(bundle.getString("dlg.wgs.lon") + " (-180° - 180°)");
        TextField tfLat = new TextField(Globals.propman.getProperty(Globals.COORD_LAT, lat + ""));
        TextField tfLon = new TextField(Globals.propman.getProperty(Globals.COORD_LON, lon + ""));

        tfLat.setPrefWidth(200);

        gridPane.addRow(0, lbLat, tfLat);
        gridPane.addRow(1, lbLon, tfLon);

        getDialogPane().setContent(gridPane);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                double lat = -9999;
                double lon = -9999;
                try {
                    lat = Double.valueOf(tfLat.getText());
                    lon = Double.valueOf(tfLon.getText());
                } catch (NumberFormatException ex) {
                    _log.error(ex.getMessage());
                }
                return new PosPOJO(lat, lon);
            }
            return null;
        });
    }
}
