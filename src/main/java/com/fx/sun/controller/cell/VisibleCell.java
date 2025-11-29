package com.fx.sun.controller.cell;

import com.fx.sun.pojo.TablePOJO;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

/**
 *
 * @author pscha
 */
public class VisibleCell extends TableCell<TablePOJO, Double> {

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        if (empty||item==null) {
            setText(null);
            setGraphic(null);
            setStyle("");
        } else if ( !empty) {

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            Label label = new Label(item.toString());
            label.setStyle("-fx-font: 10px Arial");

            if (item < 0) {
                hBox.getStyleClass().add("orange");
            }
            hBox.getChildren().add(label);
            setGraphic(hBox);
            setText("");
        }
    }
}
