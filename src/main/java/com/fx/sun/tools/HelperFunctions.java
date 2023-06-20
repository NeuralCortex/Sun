package com.fx.sun.tools;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelperFunctions {

    private static final Logger _log = LogManager.getLogger(HelperFunctions.class);

    public static void centerWindow(Window window) {
        window.addEventHandler(WindowEvent.WINDOW_SHOWN, (WindowEvent event) -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
        });
    }

    public Node loadFxml(ResourceBundle bundle, String path, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path), bundle);
            loader.setController(controller);
            Node node = loader.load();
            return node;
        } catch (IOException ex) {
            _log.error(ex.getMessage());
        }
        return null;
    }
}
