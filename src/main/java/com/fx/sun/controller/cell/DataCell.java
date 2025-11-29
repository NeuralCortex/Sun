package com.fx.sun.controller.cell;

import com.fx.sun.pojo.DatePOJO;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.shredzone.commons.suncalc.MoonTimes;
import org.shredzone.commons.suncalc.SunTimes;

/**
 *
 * @author pscha
 */
public class DataCell extends TableCell<DatePOJO, LocalDate> {

    private double lat;
    private double lon;
    private LocalDate now = LocalDate.now();
    private LocalDate month = null;

    public DataCell(double lat, double lon, LocalDate month) {
        this.lat = lat;
        this.lon = lon;
        this.month = month;
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
            VBox vBox2 = new VBox();
            HBox hBox = new HBox();

            Label datum = new Label(simpleDateFormat.format(calendar.getTime()));
            if (item.equals(now)) {
                datum.getStyleClass().add("text-red");
            }

            //new
            //Calendar[] sunriseSunset = ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset(calendar, lat, lon);
            SunTimes zmSun = SunTimes.compute().on(date).at(lat, lon).execute();
            MoonTimes zmMoon = MoonTimes.compute().on(date).at(lat, lon).execute();

            /*
            Label lbSunrise = new Label("▲" + calculator.getOfficialSunriseForDate(calendar));
            Label lbSunset = new Label("▼" + calculator.getOfficialSunsetForDate(calendar));
             */
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.GERMANY);

            Label lbSunrise = new Label("S▲" + zmSun.getRise().format(dateTimeFormatter) + "  M▲" + zmMoon.getRise().format(dateTimeFormatter));
            Label lbSunset = new Label("S▼" + zmSun.getSet().format(dateTimeFormatter) + "  M▼" + zmMoon.getSet().format(dateTimeFormatter));

            /*
            simpleDateFormat = new SimpleDateFormat("HH:mm",Locale.GERMANY);
            
            Label lbSunrise = new Label("▲" + simpleDateFormat.format(sunriseSunset==null?"99:99":sunriseSunset[0].getTime()));
            Label lbSunset = new Label("▼" + simpleDateFormat.format(sunriseSunset==null?"99:99":sunriseSunset[1].getTime()));
             */

            vBox2.getChildren().addAll(lbSunrise, lbSunset);

            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.getChildren().add(vBox2);

            vBox.getChildren().addAll(datum, hBox);

            VBox.setMargin(datum, new Insets(10));
            VBox.setMargin(hBox, new Insets(0, 10, 10, 10));

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
