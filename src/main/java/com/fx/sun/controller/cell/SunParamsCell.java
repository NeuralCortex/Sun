package com.fx.sun.controller.cell;

import com.fx.sun.pojo.DatePOJO;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;
import org.shredzone.commons.suncalc.SunPosition;

/**
 *
 * @author pscha
 */
public class SunParamsCell extends TableCell<DatePOJO, LocalDate> {

    private final double lat;
    private final double lon;

    private final LocalDate now = LocalDate.now();
    private LocalDate month = null;

    private final int hour;
    private final int min;
    private final int sec;

    private final ResourceBundle bundle;

    public SunParamsCell(double lat, double lon, LocalDate month, int hour, int min, int sec, ResourceBundle bundle) {
        this.lat = lat;
        this.lon = lon;
        this.month = month;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
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

            Date date = Date.from(item.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd.MM.yyyy");

            VBox vBox = new VBox();
            VBox infos = new VBox();
           

            Label datum = new Label(simpleDateFormat.format(calendar.getTime()));
          
            if (item.equals(now)) {
                datum.getStyleClass().add("text-red");
            }

            LocalDateTime t = item.atTime(hour, min, sec);

            SunPosition sunPosition = SunPosition.compute().on(t).at(lat, lon).execute();

            DecimalFormat decimalFormat = new DecimalFormat("#.00");

            Label lbAzi = new Label(bundle.getString("azi") + ": \t" + decimalFormat.format(sunPosition.getAzimuth()) + "°");
            Label lbAlti = new Label(bundle.getString("alti") + ": \t" + decimalFormat.format(sunPosition.getAltitude()) + "°");
            Label lbDist = new Label(bundle.getString("dist") + ": \t" + decimalFormat.format(sunPosition.getDistance()) + " km");

            infos.setAlignment(Pos.CENTER_LEFT);
            infos.getChildren().addAll(lbAzi, lbAlti, lbDist);

            vBox.getChildren().addAll(datum, infos);

            VBox.setMargin(datum, new Insets(10, 10, 5, 10));
            VBox.setMargin(infos, new Insets(0, 10, 10, 10));

            if (item.getMonth().equals(month.getMonth())) {
                vBox.getStyleClass().add("orange");
            }

            setGraphic(vBox);
            setText(null);
        }
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }
}
