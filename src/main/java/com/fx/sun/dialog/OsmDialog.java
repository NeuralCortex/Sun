package com.fx.sun.dialog;

import com.fx.sun.Globals;
import com.fx.sun.pojo.PosPOJO;
import com.fx.sun.tools.HelperFunctions;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

/**
 *
 * @author pscha
 */
public class OsmDialog extends Dialog<PosPOJO> implements Initializable{

    @FXML
    private BorderPane borderPane;
    @FXML
    private Label lbLat;
    @FXML
    private Label lbLon;
    @FXML
    private TextField tfLat;
    @FXML
    private TextField tfLon;

    private static final Logger _log = LogManager.getLogger(OsmDialog.class);
    private final JXMapViewer mapViewer = new JXMapViewer();
    private final List<Painter<JXMapViewer>> painters = new ArrayList<>();
    private SwingNode swingNode;
    //Kriegerehrenmal Zella-Mehlis
    private double lat = 50.659338995337976;
    private double lon = 10.665138248049024;

    private final ResourceBundle bundle;

    public OsmDialog(ResourceBundle bundle) {
        this.bundle = bundle;
        init();
        //initOsmMap();
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

        ButtonType saveButtonType = new ButtonType(bundle.getString("dlg.wgs.btn.save"), ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Node node = loadDialog(bundle, Globals.DLG_OSM_PATH, this);
        getDialogPane().setContent(node);

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

    private void initOsmMap() {

        TileFactoryInfo tileFactoryInfo = new OSMTileFactoryInfo();
        DefaultTileFactory defaultTileFactory = new DefaultTileFactory(tileFactoryInfo);
        defaultTileFactory.setThreadPoolSize(Runtime.getRuntime().availableProcessors());
        mapViewer.setTileFactory(defaultTileFactory);

        final JLabel labelAttr = new JLabel();
        mapViewer.setLayout(new BorderLayout());
        mapViewer.add(labelAttr, BorderLayout.SOUTH);
        labelAttr.setText(defaultTileFactory.getInfo().getAttribution() + " - " + defaultTileFactory.getInfo().getLicense());

        // Set the focus
        GeoPosition zellaMehlis = new GeoPosition(lat, lon);

        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(zellaMehlis);

        // Add interactions
        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        MousePositionListener mousePositionListener = new MousePositionListener(mapViewer);
        mousePositionListener.setGeoPosListener((GeoPosition geoPosition) -> {
            Platform.runLater(() -> {
                //mainController.getLbStatus().setText("Longitude: " + geoPosition.getLongitude() + " Latitude: " + geoPosition.getLatitude());
            });
        });
        mapViewer.addMouseMotionListener(mousePositionListener);

        //Popup
        MousePopupListener mousePopupListener = new MousePopupListener(mapViewer);
        mousePopupListener.setGeoClipboard((GeoPosition geoPosition) -> {
            Platform.runLater(() -> {
                tfLon.setText(geoPosition.getLongitude() + "");
                tfLat.setText(geoPosition.getLatitude() + "");

                painters.clear();
                mapViewer.setAddressLocation(geoPosition);

                painters.add(new CrossPainter(geoPosition));
                CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                mapViewer.setOverlayPainter(painter);

                mapViewer.repaint();
            });
        });
        mapViewer.addMouseListener(mousePopupListener);

        swingNode = new SwingNode();
        VBox.setMargin(swingNode, new Insets(0, 0, 10, 0));
        VBox.setVgrow(swingNode, Priority.ALWAYS);

        try {
            SwingUtilities.invokeAndWait(() -> {
                swingNode.setContent(mapViewer);
                swingNode.requestFocus();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        VBox vBox = new VBox(swingNode);

        borderPane.setCenter(vBox);

        new Timer().schedule(new TimerTask() {
            public void run() {
                swingNode.getContent().repaint();
            }
        }, 100L);
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        initOsmMap();

        lbLat.setText(bundle.getString("dlg.wgs.lat") + " (-90° - 90°)");
        lbLon.setText(bundle.getString("dlg.wgs.lon") + " (-180° - 180°)");

        tfLat.setText(Globals.propman.getProperty(Globals.COORD_LAT, lat + ""));
        tfLon.setText(Globals.propman.getProperty(Globals.COORD_LON, lon + ""));

        tfLat.setOnKeyPressed(e -> {
            getClipboardFromGoogleMaps(e);
        });

        tfLon.setOnKeyPressed(e -> {
            getClipboardFromGoogleMaps(e);
        });

        lat = Double.valueOf(Globals.propman.getProperty(Globals.COORD_LAT));
        lon = Double.valueOf(Globals.propman.getProperty(Globals.COORD_LON));

        painters.clear();
        GeoPosition geoPosition = new GeoPosition(lat, lon);
        mapViewer.setAddressLocation(geoPosition);

        painters.add(new CrossPainter(geoPosition));
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mapViewer.repaint();
            }
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
