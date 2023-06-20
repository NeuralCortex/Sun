package com.fx.sun.tools;

import com.fx.sun.controller.GraphAnchorController;
import com.fx.sun.pojo.DatePosPOJO;
import com.fx.sun.pojo.TimePOJO;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.shredzone.commons.suncalc.SunTimes;

/**
 *
 * @author pscha
 */
public class SunriseAnchorPane extends AnchorPane {

    private final int offsetX = 70;
    private final int offsetY = 30;
    private int daysInYear = 0;
    private double mx = 0;
    private double my = 0;
    private List<DatePosPOJO> listMonths;
    private List<TimePOJO> listRise;
    private List<TimePOJO> listSunset;
    private LocalDate now;
    private double lat;
    private double lon;

    private Font font = new Font("Arial", 10.0f);

    public SunriseAnchorPane() {
        setMinSize(0, 0);

        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public void redraw() {
        clear();

        if (listMonths != null) {
            drawCoordSys();
        }
        if (listRise != null) {
            drawGraph(GraphAnchorController.TYPE.SUNRISE);
        }
        if (listSunset != null) {
            drawGraph(GraphAnchorController.TYPE.SUNSET);
        }
        if(listMonths!=null){
            showMark(mx, my);
        }
    }

    private void clear() {
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.WHITE);
        getChildren().clear();
        getChildren().add(rectangle);
    }

    private void drawCoordSys() {
        double x = offsetX + (getWidth() - 2 * offsetX) * (1 / (double) daysInYear);
        //double x = offsetX;
        double y = offsetY;
        double width = getWidth() - 2 * offsetX;
        double height = getHeight() - 2 * offsetY;

        for (int i = 0; i <= 24; i++) {
            //double x0 = offsetX + (getWidth() - 2 * offsetX) * (1 / (double) daysInYear);
            double x0 = offsetX;
            double y0 = offsetY + (getHeight() - 2 * offsetY) * (i / 24.0f);
            DatePosPOJO day = listMonths.get(listMonths.size() - 1);
            //double x1 = (getWidth() - offsetX);
            double x1 = offsetX + (getWidth() - 2 * offsetX) * (day.getPos() / (double) daysInYear);
            double y1 = offsetY + (getHeight() - 2 * offsetY) * (i / 24.0f);
            Line line = new Line(x0, y0, x1, y1);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1.0f);
            getChildren().add(line);

            String time = String.format("%02d", 24 - i) + ":" + String.format("%02d", 0);
            Text text = new Text(time);
            Bounds bounds = text.getLayoutBounds();
            text.setFont(font);
            text.setX(offsetX - bounds.getWidth() - 5);
            text.setY(offsetY + (getHeight() - 2 * offsetY) * (i / 24.0f));
            getChildren().add(text);
        }

        for (int i = 0; i < listMonths.size(); i++) {
            DatePosPOJO day = listMonths.get(i);

            double x0 = offsetX + (getWidth() - 2 * offsetX) * (day.getPos() / (double) daysInYear);
            double y0 = offsetY;
            double x1 = offsetX + (getWidth() - 2 * offsetX) * (day.getPos() / (double) daysInYear);
            double y1 = getHeight() - offsetY;
            Line line = new Line(x0, y0, x1, y1);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1.0f);
            if (i >= 0) {
                getChildren().add(line);
            }

            if (i < listMonths.size() - 1) {
                x = offsetX + (getWidth() - 2 * offsetX) * (day.getPos() / (double) daysInYear);
                y = getHeight() - offsetY + 15;
                Text text = new Text(day.getDate().format(DateTimeFormatter.ofPattern("EEE dd.MM.yyyy", Locale.getDefault())));
                text.setFont(font);
                text.setX(x);
                text.setY(y);
                getChildren().add(text);
            }
        }
    }

    private void drawGraph(GraphAnchorController.TYPE type) {
        Path path = new Path();
        path.setStrokeWidth(2.0f);

        if (type == GraphAnchorController.TYPE.SUNRISE) {
            path.setStroke(Color.RED);
            for (int i = 0; i < listRise.size(); i++) {
                TimePOJO timePOJO = listRise.get(i);
                double xPos = offsetX + (timePOJO.getPos()) * ((getWidth() - 2 * offsetX) / daysInYear);
                double yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (timePOJO.getTime() / 24.0f));
                if (i == 0) {
                    xPos = offsetX + (0) * ((getWidth() - 2 * offsetX) / daysInYear);
                    MoveTo moveTo = new MoveTo(xPos, yPos);
                    path.getElements().add(moveTo);
                }
                LineTo lineTo = new LineTo(xPos, yPos);
                path.getElements().add(lineTo);
            }
        } else {
            path.setStroke(Color.BLUE);
            for (int i = 0; i < listSunset.size(); i++) {
                TimePOJO timePOJO = listSunset.get(i);
                double xPos = offsetX + (timePOJO.getPos()) * ((getWidth() - 2 * offsetX) / daysInYear);
                double yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (timePOJO.getTime() / 24.0f));
                if (i == 0) {
                    xPos = offsetX + (0) * ((getWidth() - 2 * offsetX) / daysInYear);
                    MoveTo moveTo = new MoveTo(xPos, yPos);
                    path.getElements().add(moveTo);
                }
                LineTo lineTo = new LineTo(xPos, yPos);
                path.getElements().add(lineTo);
            }
        }
        getChildren().add(path);
    }

    private void showMark(double x, double y) {
        double xL = offsetX;
        double yL = offsetY;
        double height = getHeight() - 2 * offsetY;

        DatePosPOJO daye = listMonths.get(listMonths.size() - 1);
        double xLE = offsetX + (getWidth() - 2 * offsetX) * (daye.getPos() / (double) daysInYear);

        if (x > xL && x < xLE && y > yL && y < (getHeight() - offsetY)) {

            int day = (int) ((x - offsetX) / (getWidth() - 2 * offsetX) * (double) (daysInYear));

            Line line = new Line(x, offsetY, x, offsetY + height);
            line.setStrokeWidth(2.0f);
            getChildren().add(line);

            LocalDate selDate = now.with(TemporalAdjusters.firstDayOfYear()).plusDays(day);

            SunTimes visual = SunTimes.compute().on(selDate).at(lat, lon).oneDay().execute();

            Duration diffDay = Duration.between(visual.getRise(), visual.getSet());
            Duration diffNight = Duration.between(visual.getRise(), visual.getSet()).minusHours(24);

            Text text = new Text(selDate.format(DateTimeFormatter.ofPattern("EEE dd.MM.yyyy", Locale.getDefault()))
                    + " Day: " + InfoAnchorPage.getTimeDiff(diffDay)
                    + " Night: " + InfoAnchorPage.getTimeDiff(diffNight)
            );
            text.setFont(font);
            Bounds bounds = text.getLayoutBounds();
            if (selDate.getMonth().getValue() > 10) {
                x = x - bounds.getWidth();
            }
            text.setX(x);
            text.setY(offsetY - bounds.getHeight());
            getChildren().add(text);
        }
    }

    public void setDaysInYear(int daysInYear) {
        this.daysInYear = daysInYear;
    }

    public void setListMonths(List<DatePosPOJO> listMonths) {
        this.listMonths = listMonths;
    }

    public void setListRise(List<TimePOJO> listRise) {
        this.listRise = listRise;
    }

    public void setListSunset(List<TimePOJO> listSunset) {
        this.listSunset = listSunset;
    }

    public void setMx(double mx) {
        this.mx = mx;
    }

    public void setMy(double my) {
        this.my = my;
    }

    public void setNow(LocalDate now) {
        this.now = now;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
