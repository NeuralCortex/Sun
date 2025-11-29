package com.fx.sun.controller.cell;

import com.fx.sun.pojo.DatePOJO;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;

/**
 *
 * @author pscha
 */
public class KwCell extends TableCell<DatePOJO, LocalDate> {

    private ResourceBundle bundle;

    public KwCell(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
            setStyle("");
        } else if (item != null && !empty) {

            Label lbKW = new Label();
            VBox vBox = new VBox();

            LocalDate localDate = LocalDate.of(item.getYear(), item.getMonth().getValue(), item.getDayOfMonth());
            int kw = localDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            String formatted = String.format("%02d", kw);

            lbKW.setText(bundle.getString("col.kw.short") + ": " + formatted);

            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(lbKW);

            setGraphic(vBox);
            setText(null);
        }
    }
}
