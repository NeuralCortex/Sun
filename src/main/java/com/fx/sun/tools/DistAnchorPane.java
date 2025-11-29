package com.fx.sun.tools;

import com.fx.sun.controller.tabs.DistController;
import com.fx.sun.pojo.DatePosPOJO;
import com.fx.sun.pojo.TimePOJO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
import org.shredzone.commons.suncalc.MoonPosition;
import org.shredzone.commons.suncalc.SunPosition;

/**
 *
 * @author pscha
 */
public class DistAnchorPane extends AnchorPane {

    private final int offsetX = 90;
    private final int offsetY = 30;
    private int daysInYear = 0;
    private double mx = 0;
    private double my = 0;
    private List<DatePosPOJO> listMonths;
    private List<TimePOJO> listDist;
    private LocalDate now;
    private double lat;
    private double lon;

    private double min;
    private double max;

    private int globMin;
    private int globMax;

    private final Font font = new Font("Arial", 10.0f);
    private final DistController.TYPE type;

    public DistAnchorPane(DistController.TYPE type) {
        this.type = type;
        setMinSize(0, 0);

        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public void redraw() {
        clear();
        
        double stepping=10000.0f;

        globMax = (int) (Math.ceil(max / stepping) * stepping);
        globMin = (int) (Math.floor(min / stepping) * stepping);

        if (type == DistController.TYPE.SUN) {
            
            stepping=500000.0f;
            
            globMax = (int) (Math.ceil(max / stepping) * stepping);
            globMin = (int) (Math.floor(min / stepping) * stepping);
        }

        drawCoordSys();
        drawGraph();

        showMark(mx, my);
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
        
        int stepping=2500;
        if(type==DistController.TYPE.SUN){
            stepping=250000;
        }

        for (int i = 0; i <= globMax - globMin; i += stepping) {
            //double x0 = offsetX + (getWidth() - 2 * offsetX) * (1 / (double) daysInYear);
            double x0 = offsetX;
            double y0 = offsetY + (getHeight() - 2 * offsetY) * (i / ((double) globMax - (double) globMin));
            //System.out.println("i"+(i/1000000.0f));
            DatePosPOJO day = listMonths.get(listMonths.size() - 1);
            //double x1 = (getWidth() - offsetX);
            double x1 = offsetX + (getWidth() - 2 * offsetX) * (day.getPos() / (double) daysInYear);
            double y1 = offsetY + (getHeight() - 2 * offsetY) * (i / ((double) globMax - (double) globMin));
            Line line = new Line(x0, y0, x1, y1);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1.0f);
            getChildren().add(line);

            String time = String.format("%06d", globMax - i)+" km";
            Text text = new Text(time);
            Bounds bounds = text.getLayoutBounds();
            text.setFont(font);
            text.setX(offsetX - bounds.getWidth() - 5);
            text.setY(offsetY + (getHeight() - 2 * offsetY) * (i / ((double) globMax - (double) globMin)));
            getChildren().add(text);
        }

        for (int i = 0; i < listMonths.size(); i++) {
            DatePosPOJO day = listMonths.get(i);

            double x0 = offsetX + (getWidth() - 2 * offsetX) * ((day.getPos()) / (double) (daysInYear));
            double y0 = offsetY;
            double x1 = offsetX + (getWidth() - 2 * offsetX) * ((day.getPos()) / (double) (daysInYear));
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

    private void drawGraph() {
        Path path = new Path();
        path.setStroke(Color.RED);
        path.setStrokeWidth(2.0f);

        //First Element
        double xPos = offsetX + (listDist.get(0).getPos()) * ((getWidth() - 2 * offsetX) / daysInYear);
        double yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (listDist.get(0).getTime() - globMin) / (globMax - globMin));

        MoveTo moveTo = new MoveTo(xPos, yPos);
        path.getElements().add(moveTo);

        for (int i = 0; i < listDist.size(); i++) {
            TimePOJO timePOJO = listDist.get(i);

            xPos = offsetX + (timePOJO.getPos()) * ((getWidth() - 2 * offsetX) / daysInYear);
            double sf = (timePOJO.getTime() - globMin) / (globMax - globMin);
            yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * sf);

            LineTo lineTo = new LineTo(xPos, yPos);
            path.getElements().add(lineTo);
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

            String distance;
            if (type == DistController.TYPE.MOON) {
                MoonPosition moonPosition = MoonPosition.compute().on(selDate).at(lat, lon).execute();
                distance = String.format("%06.2f", moonPosition.getDistance()) + " km";
            } else {
                SunPosition sunPosition = SunPosition.compute().on(selDate).at(lat, lon).execute();
                distance = String.format("%09.2f", sunPosition.getDistance()) + " km";
            }

            Text text = new Text(selDate.format(DateTimeFormatter.ofPattern("EEE dd.MM.yyyy", Locale.getDefault()))
                    + " Distance: " + distance
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

    public void setListDist(List<TimePOJO> listDist, double min, double max) {
        this.listDist = listDist;
        this.min = min;
        this.max = max;
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

    public void setMx(double mx) {
        this.mx = mx;
    }

    public void setMy(double my) {
        this.my = my;
    }
}
