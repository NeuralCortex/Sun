package com.fx.sun.tools;

import com.fx.sun.pojo.EleTimePOJO;
import com.fx.sun.pojo.RosePOJO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author pscha
 */
public class SunChartPolarAnchorPage extends AnchorPane {

    private double radius;
    private double offsetX = 50.0f;
    private double offsetY = 50.0f;
    private double centerX;
    private double centerY;
    private double lat;
    private double lon;

    private HashMap<Integer, List<EleTimePOJO>> mapSonnenStand = new HashMap<>();
    private HashMap<Integer, List<EleTimePOJO>> mapSchleifen = new HashMap<>();
    private EleTimePOJO eleTimePOJOMark;

    private Font font = new Font("Arial", 10.0f);

    public SunChartPolarAnchorPage() {
        setMinSize(0, 0);
        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public void redraw() {
        clear();

        radius = getHeight() / 2.0f - offsetY;
        if (getWidth() < getHeight()) {
            radius = getWidth() / 2.0f - offsetX;
        }

        centerX = getWidth() / 2.0f;
        centerY = getHeight() / 2.0f;

        //drawCircle();
        drawCoordSys();
        //drawSchleifen();
        if (lat > 0) {
            drawSonnenStandNord();
            drawSchleifenNord();
            drawMarkNord();
        } else {
            drawSonnenStandSued();
            drawSchleifenSued();
            drawMarkSued();
        }
    }

    private void clear() {
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.WHITE);
        getChildren().clear();
        getChildren().add(rectangle);
    }

    private void drawCoordSys() {

        for (int i = 0; i <= 90; i += 30) {
            double sf = i / 90.0f;
            drawCircle(radius * sf);
        }
        double offset = 90 / 4.0f;
        for (double i = 0; i < 360; i += offset) {
            float x = (float) (radius * Math.cos(Math.toRadians(i)));
            float y = (float) (radius * Math.sin(Math.toRadians(i)));
            Line line = new Line(centerX, centerY, centerX + x, centerY + y);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1.0f);
            getChildren().add(line);
        }

        double angle = 0;
        List<RosePOJO> list = new ArrayList<>();
        list.add(new RosePOJO(angle, "N"));
        list.add(new RosePOJO(angle += offset, "NNO"));
        list.add(new RosePOJO(angle += offset, "NO"));
        list.add(new RosePOJO(angle += offset, "ONO"));
        list.add(new RosePOJO(angle += offset, "O"));
        list.add(new RosePOJO(angle += offset, "OSO"));
        list.add(new RosePOJO(angle += offset, "SO"));
        list.add(new RosePOJO(angle += offset, "SSO"));
        list.add(new RosePOJO(angle += offset, "S"));
        list.add(new RosePOJO(angle += offset, "SSW"));
        list.add(new RosePOJO(angle += offset, "SW"));
        list.add(new RosePOJO(angle += offset, "WSW"));
        list.add(new RosePOJO(angle += offset, "W"));
        list.add(new RosePOJO(angle += offset, "WNW"));
        list.add(new RosePOJO(angle += offset, "NW"));
        list.add(new RosePOJO(angle += offset, "NNW"));

        offset = 5.0f;
        for (int i = 0; i < list.size(); i++) {
            RosePOJO rosePOJO = list.get(i);

            angle = rosePOJO.getId() - 90;

            Text text = new Text(rosePOJO.getName());
            text.setFont(font);
            Bounds bounds = text.getLayoutBounds();

            double x = 0;
            double y = 0;

            if (angle > 0 && angle <= 90) {
                y = bounds.getHeight();
            }
            if (angle > 90 && angle <= 180) {
                x = bounds.getWidth();
                y = bounds.getHeight();
            }
            if (angle > 180) {
                x = bounds.getWidth();
            }

            x += offset;
            y += offset;

            double x0 = centerX + (x + radius) * Math.cos(Math.toRadians(angle));
            double y0 = centerY + (y + radius) * Math.sin(Math.toRadians(angle));

            text.setX(x0);
            text.setY(y0);
            getChildren().add(text);
        }

        angle = 0;
        list = new ArrayList<>();
        list.add(new RosePOJO(angle, "Zenit"));
        list.add(new RosePOJO(angle += 30, "60°"));
        list.add(new RosePOJO(angle += 30, "30°"));
        list.add(new RosePOJO(angle += 30, "Horizon"));

        offset = 5.0f;
        for (int i = 0; i < list.size(); i++) {
            RosePOJO rosePOJO = list.get(i);

            angle = rosePOJO.getId();

            Text text = new Text(rosePOJO.getName());
            text.setFont(font);
            text.setFill(Color.RED);
            Bounds bounds = text.getLayoutBounds();

            double x = centerX;
            double y = centerY - (angle / 90.0f * radius);

            y += bounds.getHeight();

            x += offset;
            y += offset;

            text.setX(x);
            text.setY(y);
            getChildren().add(text);
        }
    }

    private void drawCircle(double radius) {
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1.0f);
        getChildren().addAll(circle);
    }

    private void drawSonnenStandNord(List<EleTimePOJO> listSonnenStand, int hour) {
        Path path = new Path();
        path.setStroke(Color.RED);
        path.setStrokeWidth(2.0f);

        if (hour > 1) {
            path.setStroke(Color.BLACK);
            path.setStrokeWidth(0.5f);
        }

        //WIKI
        //x=r*cos(a)
        //y=r*sin(a)
        //First Element
        int pos = 0;
        for (int i = 0; i < listSonnenStand.size() - 1; i++) {
            EleTimePOJO first = listSonnenStand.get(i);
            EleTimePOJO last = listSonnenStand.get(i + 1);

            if (first.getAltitude() < 0 && last.getAltitude() > 0) {
                pos = i;
                break;
            }

        }

        EleTimePOJO first = listSonnenStand.get(pos);

        double r = radius - first.getAltitude() / (90) * radius;
        double xPos = r * Math.cos(Math.toRadians(first.getAzimuth() - 90)) + centerX;
        double yPos = r * Math.sin(Math.toRadians(first.getAzimuth() - 90)) + centerY;

        MoveTo moveTo = new MoveTo(xPos, yPos);
        path.getElements().add(moveTo);

        for (int i = 0; i < listSonnenStand.size(); i += 1) {
            EleTimePOJO eleTimePOJO = listSonnenStand.get(i);

            r = radius - eleTimePOJO.getAltitude() / (90) * radius;
            xPos = r * Math.cos(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerX;
            yPos = r * Math.sin(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerY;

            LocalDateTime localDateTime;
            //TODO Check Stunden
            localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

            if (eleTimePOJO.getAltitude() > 0) {

                if (hour == 0) {
                    for (int k = 0; k < 24; k++) {
                        if (localDateTime.getHour() == k && localDateTime.getMinute() == 0) {

                            Text text = new Text(k + "h");
                            text.setFont(font);
                            text.setX(xPos);
                            text.setY(yPos);
                            getChildren().add(text);

                            Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                            getChildren().add(circle);
                        }
                    }
                }

                LineTo lineTo = new LineTo(xPos, yPos);
                if (hour > 1) {
                    path.getStrokeDashArray().addAll(0.0, 10.0, 10.0, 0.0);
                }
                path.getElements().add(lineTo);
            }
        }
        getChildren().add(path);
    }

    private void drawSonnenStandNord() {

        for (Integer hour : mapSonnenStand.keySet()) {

            Path path = new Path();
            path.setStroke(Color.RED);
            path.setStrokeWidth(2.0f);

            if (hour > 1) {
                path.setStroke(Color.BLACK);
                path.setStrokeWidth(0.5f);
            }

            List<EleTimePOJO> listSonnenStand = mapSonnenStand.get(hour);

            //WIKI
            //x=r*cos(a)
            //y=r*sin(a)
            //First Element
            int pos = 0;
            for (int i = 0; i < listSonnenStand.size() - 1; i++) {
                EleTimePOJO first = listSonnenStand.get(i);
                EleTimePOJO last = listSonnenStand.get(i + 1);

                if (first.getAltitude() < 0 && last.getAltitude() > 0) {
                    pos = i;
                    break;
                }

            }

            EleTimePOJO first = listSonnenStand.get(pos);

            double r = radius - first.getAltitude() / (90) * radius;
            double xPos = r * Math.cos(Math.toRadians(first.getAzimuth() - 90)) + centerX;
            double yPos = r * Math.sin(Math.toRadians(first.getAzimuth() - 90)) + centerY;

            MoveTo moveTo = new MoveTo(xPos, yPos);
            path.getElements().add(moveTo);

            for (int i = 0; i < listSonnenStand.size(); i += 1) {
                EleTimePOJO eleTimePOJO = listSonnenStand.get(i);

                r = radius - eleTimePOJO.getAltitude() / (90) * radius;
                xPos = r * Math.cos(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerX;
                yPos = r * Math.sin(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerY;

                LocalDateTime localDateTime;
                //TODO Check Stunden
                localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

                if (eleTimePOJO.getAltitude() > 0) {

                    if (hour == 0) {
                        for (int k = 0; k < 24; k++) {
                            if (localDateTime.getHour() == k && localDateTime.getMinute() == 0) {

                                Text text = new Text(k + "h");
                                text.setFont(font);
                                text.setX(xPos);
                                text.setY(yPos);
                                getChildren().add(text);

                                Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                                getChildren().add(circle);
                            }
                        }
                    }

                    LineTo lineTo = new LineTo(xPos, yPos);
                    if (hour > 1) {
                        path.getStrokeDashArray().addAll(0.0, 10.0, 10.0, 0.0);
                    }
                    path.getElements().add(lineTo);
                }
            }
            getChildren().add(path);
        }
    }

    private void drawMarkNord() {
        if (eleTimePOJOMark != null) {
            double r = radius - eleTimePOJOMark.getAltitude() / (90) * radius;
            double xPos = r * Math.cos(Math.toRadians(eleTimePOJOMark.getAzimuth() - 90)) + centerX;
            double yPos = r * Math.sin(Math.toRadians(eleTimePOJOMark.getAzimuth() - 90)) + centerY;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy");

            if (r <= radius) {
                Text text = new Text(dateTimeFormatter.format(eleTimePOJOMark.getLocalDateTime()));
                text.setFont(font);
                text.setX(xPos + 5);
                text.setY(yPos - 5);
                getChildren().add(text);

                Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                getChildren().add(circle);
            }
        }
    }

    private void drawMarkSued() {
        if (eleTimePOJOMark != null) {
            double r = radius - eleTimePOJOMark.getAltitude() / (90) * radius;
            double xPos = r * Math.cos(Math.toRadians(eleTimePOJOMark.getAzimuth() - 90)) + centerX;
            double yPos = r * Math.sin(Math.toRadians(eleTimePOJOMark.getAzimuth() - 90)) + centerY;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy");

            if (r <= radius) {
                Text text = new Text(dateTimeFormatter.format(eleTimePOJOMark.getLocalDateTime()));
                text.setFont(font);
                text.setX(xPos + 5);
                text.setY(yPos - 5);
                getChildren().add(text);

                Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                getChildren().add(circle);
            }
        }
    }

    private void drawSonnenStandSued(List<EleTimePOJO> listSonnenStand, int hour) {
        //Rechts
        Path path = new Path();
        path.setStroke(Color.RED);
        path.setStrokeWidth(2.0f);

        if (hour > 1) {
            path.setStroke(Color.BLACK);
            path.setStrokeWidth(0.5f);
        }

        EleTimePOJO first = listSonnenStand.get(0);

        double r = radius - first.getAltitude() / (90) * radius;
        double xPos = r * Math.cos(Math.toRadians(first.getAzimuth() - 90)) + centerX;
        double yPos = r * Math.sin(Math.toRadians(first.getAzimuth() - 90)) + centerY;

        MoveTo moveTo = new MoveTo(xPos, yPos);
        path.getElements().add(moveTo);

        for (int i = 0; i < listSonnenStand.size(); i += 1) {
            EleTimePOJO eleTimePOJO = listSonnenStand.get(i);

            if (eleTimePOJO.getAltitude() < 0) {
                break;
            }

            r = radius - eleTimePOJO.getAltitude() / (90) * radius;
            xPos = r * Math.cos(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerX;
            yPos = r * Math.sin(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerY;

            LocalDateTime localDateTime;
            localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

            if (true) {

                if (hour == 1) {
                    for (int k = 0; k < 24; k++) {
                        if (localDateTime.getHour() == k && localDateTime.getMinute() == 0) {

                            Text text = new Text(k + "h");
                            text.setFont(font);
                            text.setX(xPos + 5);
                            text.setY(yPos - 5);
                            getChildren().add(text);

                            Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                            getChildren().add(circle);
                        }
                    }
                }

                LineTo lineTo = new LineTo(xPos, yPos);
                if (hour > 1) {
                    path.getStrokeDashArray().addAll(0.0, 10.0, 10.0, 0.0);
                }
                path.getElements().add(lineTo);
            }
        }
        getChildren().add(path);

        //Links
        Path pathL = new Path();
        pathL.setStroke(Color.RED);
        pathL.setStrokeWidth(2.0f);

        if (hour > 1) {
            pathL.setStroke(Color.BLACK);
            pathL.setStrokeWidth(0.5f);
        }

        first = listSonnenStand.get(0);

        r = radius - first.getAltitude() / (90) * radius;
        xPos = r * Math.cos(Math.toRadians(first.getAzimuth() - 90)) + centerX;
        yPos = r * Math.sin(Math.toRadians(first.getAzimuth() - 90)) + centerY;

        moveTo = new MoveTo(xPos, yPos);
        pathL.getElements().add(moveTo);

        for (int i = listSonnenStand.size() - 1; i > 0; i -= 1) {
            EleTimePOJO eleTimePOJO = listSonnenStand.get(i);

            if (eleTimePOJO.getAltitude() < 0) {
                break;
            }

            r = radius - eleTimePOJO.getAltitude() / (90) * radius;
            xPos = r * Math.cos(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerX;
            yPos = r * Math.sin(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerY;

            LocalDateTime localDateTime;
            localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

            if (true) {

                if (hour == 1) {
                    for (int k = 0; k < 24; k++) {
                        if (localDateTime.getHour() == k && localDateTime.getMinute() == 0) {

                            Text text = new Text(k + "h");
                            text.setFont(font);
                            text.setX(xPos + 5);
                            text.setY(yPos - 5);
                            getChildren().add(text);

                            Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                            getChildren().add(circle);
                        }
                    }
                }

                LineTo lineTo = new LineTo(xPos, yPos);
                if (hour > 1) {
                    pathL.getStrokeDashArray().addAll(0.0, 10.0, 10.0, 0.0);
                }
                pathL.getElements().add(lineTo);
            }
        }
        getChildren().add(pathL);
    }

    private void drawSonnenStandSued() {

        for (Integer hour : mapSonnenStand.keySet()) {

            //Rechts
            Path path = new Path();
            path.setStroke(Color.RED);
            path.setStrokeWidth(2.0f);

            if (hour > 1) {
                path.setStroke(Color.BLACK);
                path.setStrokeWidth(0.5f);
            }

            List<EleTimePOJO> listSonnenStand = mapSonnenStand.get(hour);

            EleTimePOJO first = listSonnenStand.get(0);

            double r = radius - first.getAltitude() / (90) * radius;
            double xPos = r * Math.cos(Math.toRadians(first.getAzimuth() - 90)) + centerX;
            double yPos = r * Math.sin(Math.toRadians(first.getAzimuth() - 90)) + centerY;

            MoveTo moveTo = new MoveTo(xPos, yPos);
            path.getElements().add(moveTo);

            for (int i = 0; i < listSonnenStand.size(); i += 1) {
                EleTimePOJO eleTimePOJO = listSonnenStand.get(i);

                if (eleTimePOJO.getAltitude() < 0) {
                    break;
                }

                r = radius - eleTimePOJO.getAltitude() / (90) * radius;
                xPos = r * Math.cos(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerX;
                yPos = r * Math.sin(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerY;

                LocalDateTime localDateTime;
                localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

                if (true) {

                    if (hour == 1) {
                        for (int k = 0; k < 24; k++) {
                            if (localDateTime.getHour() == k && localDateTime.getMinute() == 0) {

                                Text text = new Text(k + "h");
                                text.setFont(font);
                                text.setX(xPos + 5);
                                text.setY(yPos - 5);
                                getChildren().add(text);

                                Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                                getChildren().add(circle);
                            }
                        }
                    }

                    LineTo lineTo = new LineTo(xPos, yPos);
                    if (hour > 1) {
                        path.getStrokeDashArray().addAll(0.0, 10.0, 10.0, 0.0);
                    }
                    path.getElements().add(lineTo);
                }
            }
            getChildren().add(path);

            //Links
            Path pathL = new Path();
            pathL.setStroke(Color.RED);
            pathL.setStrokeWidth(2.0f);

            if (hour > 1) {
                pathL.setStroke(Color.BLACK);
                pathL.setStrokeWidth(0.5f);
            }

            first = listSonnenStand.get(0);

            r = radius - first.getAltitude() / (90) * radius;
            xPos = r * Math.cos(Math.toRadians(first.getAzimuth() - 90)) + centerX;
            yPos = r * Math.sin(Math.toRadians(first.getAzimuth() - 90)) + centerY;

            moveTo = new MoveTo(xPos, yPos);
            pathL.getElements().add(moveTo);

            for (int i = listSonnenStand.size() - 1; i > 0; i -= 1) {
                EleTimePOJO eleTimePOJO = listSonnenStand.get(i);

                if (eleTimePOJO.getAltitude() < 0) {
                    break;
                }

                r = radius - eleTimePOJO.getAltitude() / (90) * radius;
                xPos = r * Math.cos(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerX;
                yPos = r * Math.sin(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerY;

                LocalDateTime localDateTime;
                localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

                if (true) {

                    if (hour == 1) {
                        for (int k = 0; k < 24; k++) {
                            if (localDateTime.getHour() == k && localDateTime.getMinute() == 0) {

                                Text text = new Text(k + "h");
                                text.setFont(font);
                                text.setX(xPos + 5);
                                text.setY(yPos - 5);
                                getChildren().add(text);

                                Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                                getChildren().add(circle);
                            }
                        }
                    }

                    LineTo lineTo = new LineTo(xPos, yPos);
                    if (hour > 1) {
                        pathL.getStrokeDashArray().addAll(0.0, 10.0, 10.0, 0.0);
                    }
                    pathL.getElements().add(lineTo);
                }
            }
            getChildren().add(pathL);
        }
    }

    private void drawSchleifenNord() {

        for (Integer hour : mapSchleifen.keySet()) {

            Path path = new Path();
            path.setStroke(Color.BLUE);
            path.setStrokeWidth(1.0f);

            List<EleTimePOJO> listSchleife = mapSchleifen.get(hour);

            //First Element
            //WIKI
            //x=r*cos(a)
            //y=r*sin(a)
            //First Element
            int pos = 0;
            for (int i = 0; i < listSchleife.size() - 1; i++) {
                EleTimePOJO first = listSchleife.get(i);
                EleTimePOJO last = listSchleife.get(i + 1);

                if (first.getAltitude() < 0 && last.getAltitude() > 0) {
                    pos = i;
                    break;
                }
            }

            EleTimePOJO last = listSchleife.get(pos);

            double r = radius - last.getAltitude() / (90) * radius;
            double xPos = r * Math.cos(Math.toRadians(last.getAzimuth() - 90)) + centerX;
            double yPos = r * Math.sin(Math.toRadians(last.getAzimuth() - 90)) + centerY;

            MoveTo moveTo = new MoveTo(xPos, yPos);
            path.getElements().add(moveTo);

            for (int i = 0; i < listSchleife.size(); i += 1) {
                EleTimePOJO eleTimePOJO = listSchleife.get(i);

                r = radius - eleTimePOJO.getAltitude() / (90) * radius;
                xPos = r * Math.cos(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerX;
                yPos = r * Math.sin(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerY;

                if (eleTimePOJO.getAltitude() > 0) {
                    LocalDateTime localDateTime;
                    localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

                    if (localDateTime.getHour() == 12) {
                        for (int k = 0; k < 12; k++) {
                            if (localDateTime.getDayOfMonth() == 1 && localDateTime.getMonthValue() == k) {
                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy");
                                Text text = new Text(dateTimeFormatter.format(localDateTime));
                                text.setFont(font);
                                text.setX(xPos + 5);
                                text.setY(yPos - 5);
                                getChildren().add(text);

                                Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                                getChildren().add(circle);
                            }
                        }
                    }
                    
                    /*
                    float color[]=generateAdvancedColor(i/(float)listSchleife.size());
                    Circle circle = new Circle(xPos, yPos, 3.0f, Color.rgb((int)(color[0]*255), (int)(color[1]*255), (int)(color[2]*255)));
                    getChildren().add(circle);
                    */
                    LineTo lineTo = new LineTo(xPos, yPos);
                    path.getElements().add(lineTo);
                }

            }
            getChildren().add(path);
        }
    }

    private void drawSchleifenSued() {

        for (Integer hour : mapSchleifen.keySet()) {

            Path path = new Path();
            path.setStroke(Color.BLUE);
            path.setStrokeWidth(1.0f);

            List<EleTimePOJO> listSchleife = mapSchleifen.get(hour);

            //First Element
            //WIKI
            //x=r*cos(a)
            //y=r*sin(a)
            //First Element
            int pos = 0;
            for (int i = 0; i < listSchleife.size() - 1; i++) {
                EleTimePOJO first = listSchleife.get(i);
                EleTimePOJO last = listSchleife.get(i + 1);

                if (first.getAltitude() < 0 && last.getAltitude() > 0) {
                    pos = i;
                    break;
                }
            }

            EleTimePOJO last = listSchleife.get(0);

            double r = radius - last.getAltitude() / (90) * radius;
            double xPos = r * Math.cos(Math.toRadians(last.getAzimuth() - 90)) + centerX;
            double yPos = r * Math.sin(Math.toRadians(last.getAzimuth() - 90)) + centerY;

            MoveTo moveTo = new MoveTo(xPos, yPos);
            path.getElements().add(moveTo);

            for (int i = 0; i < listSchleife.size(); i += 1) {
                EleTimePOJO eleTimePOJO = listSchleife.get(i);

                r = radius - eleTimePOJO.getAltitude() / (90) * radius;
                xPos = r * Math.cos(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerX;
                yPos = r * Math.sin(Math.toRadians(eleTimePOJO.getAzimuth() - 90)) + centerY;

                if (eleTimePOJO.getAltitude() > 0) {

                    LocalDateTime localDateTime;
                    if (lat < 0) {
                        localDateTime = eleTimePOJO.getLocalDateTime().minusHours(2);
                    } else {
                        localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);
                    }

                    if (localDateTime.getHour() == 12) {
                        for (int k = 0; k < 12; k++) {
                            if (localDateTime.getDayOfMonth() == 1 && localDateTime.getMonthValue() == k) {
                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy");
                                Text text = new Text(dateTimeFormatter.format(localDateTime));
                                text.setFont(font);
                                text.setX(xPos + 5);
                                text.setY(yPos - 5);
                                getChildren().add(text);

                                Circle circle = new Circle(xPos, yPos, 3.0f, Color.BLACK);
                                getChildren().add(circle);
                            }
                        }
                    }

                    LineTo lineTo = new LineTo(xPos, yPos);
                    path.getElements().add(lineTo);
                }

            }
            getChildren().add(path);
        }
    }

    public static float[] generateAdvancedColor(float r) {
        float alpha = 1.0f;
        float[] colorQuad = new float[4];
        float green = (float) Math.abs(Math.sin(2 * r * Math.PI));
        float blue = (float) Math.abs(Math.cos(2 * r * Math.PI));
        colorQuad[0] = 0.0f;
        colorQuad[1] = green;
        colorQuad[2] = blue;
        colorQuad[3] = alpha;
        if (r >= 0.5 / 2) {
            float red = (float) Math.abs(Math.cos(2 * r * Math.PI));
            green = (float) Math.abs(Math.sin(2 * r * Math.PI));
            if (r < 0.5) {
                green = 1.0f;
            }
            colorQuad[0] = red;
            colorQuad[1] = green;
            colorQuad[2] = 0.0f;
            colorQuad[3] = alpha;
        }
        if (r >= 0.5) {
            float red = (float) Math.abs(Math.cos(2 * r * Math.PI));
            green = (float) Math.abs(Math.cos(2 * r * Math.PI));
            if (r < 0.75) {
                red = 1.0f;
            }
            colorQuad[0] = red;
            colorQuad[1] = green;
            colorQuad[2] = 0.0f;
            colorQuad[3] = alpha;
        }
        if (r >= 0.75) {
            float red = 1.0f;
            blue = (float) Math.abs(Math.cos(2 * r * Math.PI));
            colorQuad[0] = red;
            colorQuad[1] = 0.0f;
            colorQuad[2] = blue;
            colorQuad[3] = alpha;
        }

        return colorQuad;
    }

    public void setMapSonnenStand(HashMap<Integer, List<EleTimePOJO>> mapSonnenStand) {
        this.mapSonnenStand = mapSonnenStand;
    }

    public void setMapSchleifen(HashMap<Integer, List<EleTimePOJO>> mapSchleifen) {
        this.mapSchleifen = mapSchleifen;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setEleTimePOJOMark(EleTimePOJO eleTimePOJOMark) {
        this.eleTimePOJOMark = eleTimePOJOMark;
    }
}
