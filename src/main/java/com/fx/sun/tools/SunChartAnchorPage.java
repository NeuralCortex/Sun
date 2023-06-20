package com.fx.sun.tools;

import com.fx.sun.pojo.ComboBoxPOJO;
import com.fx.sun.pojo.EleTimePOJO;
import static com.fx.sun.tools.SunChartPolarAnchorPage.generateAdvancedColor;
import java.time.LocalDate;
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
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author pscha
 */
public class SunChartAnchorPage extends AnchorPane {

    private final int offsetX = 70;
    private final int offsetY = 50;
    private HashMap<Integer, List<EleTimePOJO>> mapSchleifen = new HashMap<>();
    private HashMap<Integer, List<EleTimePOJO>> mapSonnenStand = new HashMap<>();
    private final double roundUp = 90;
    private final int stepping = 10;
    private double lat;
    private double lon;

    private final Font font = Font.font("Arial", FontWeight.EXTRA_BOLD, 12.0f);

    public SunChartAnchorPage() {
        setMinSize(0, 0);

        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public void redraw() {
        clear();

        drawCoordSys();
        drawSchleifen();
        //drawSonnenStand();
        drawSonnenStandLines();
    }

    private void drawCoordSys() {

        for (int i = 0; i <= 360; i += 10) {
            double x0 = offsetX + (getWidth() - 2 * offsetX) * i / (double) 360.0f;
            double y0 = offsetY;
            double x1 = x0;
            double y1 = offsetY + (getHeight() - 2 * offsetY);
            Line line = new Line(x0, y0, x1, y1);
            line.setStrokeWidth(1.0f);
            getChildren().add(line);

            String time = String.format("%d°", i);
            Text text = new Text(time);
            Bounds bounds = text.getLayoutBounds();
            text.setFont(font);
            text.setX(offsetX + (getWidth() - 2 * offsetX) * (i / 360.0f));
            text.setY(getHeight() - offsetY - bounds.getMinY());
            getChildren().add(text);
        }

        ArrayList<ComboBoxPOJO> listOrientDesc = new ArrayList();
        listOrientDesc.add(new ComboBoxPOJO(0, "N"));
        listOrientDesc.add(new ComboBoxPOJO(90, "E"));
        listOrientDesc.add(new ComboBoxPOJO(180, "S"));
        listOrientDesc.add(new ComboBoxPOJO(270, "W"));
        listOrientDesc.add(new ComboBoxPOJO(360, "N"));

        for (int j = 0; j < listOrientDesc.size(); j++) {
            ComboBoxPOJO comboBoxPOJO = listOrientDesc.get(j);

            Text text = new Text(comboBoxPOJO.getName());
            Bounds bounds = text.getLayoutBounds();
            text.setFont(font);
            text.setX(offsetX + (getWidth() - 2 * offsetX) * (comboBoxPOJO.getId() / 360.0f));
            text.setY(getHeight() - offsetY - bounds.getMinY() + 20);
            getChildren().add(text);
        }

        for (int i = 0; i <= roundUp; i += stepping) {
            double x0 = offsetX;
            double y0 = offsetY + (getHeight() - 2 * offsetY) * (i / (double) roundUp);
            double x1 = offsetX + (getWidth() - 2 * offsetX);
            double y1 = y0;
            Line line = new Line(x0, y0, x1, y1);
            line.setStrokeWidth(1.0f);
            getChildren().add(line);

            String time = String.format("%02d°", ((int) roundUp - i));
            Text text = new Text(time);
            Bounds bounds = text.getLayoutBounds();
            text.setFont(font);
            text.setX(offsetX - bounds.getMaxX() - 5);
            text.setY(offsetY + (getHeight() - 2 * offsetY) * (i / roundUp));
            getChildren().add(text);
        }
    }

    private void drawSchleifen() {

        for (Integer hour : mapSchleifen.keySet()) {

            Path path = new Path();
            path.setStroke(Color.BLUE);
            path.setStrokeWidth(1.0f);

            List<EleTimePOJO> listSchleife = mapSchleifen.get(hour);

            //First Element
            double xPos = offsetX + (listSchleife.get(0).getAzimuth() * (getWidth() - 2 * offsetX) / (double) 360);
            double yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (listSchleife.get(0).getAltitude() / roundUp));

            if (listSchleife.get(0).getAltitude() < 0) {
                yPos = getHeight() - offsetY;
            }

            MoveTo moveTo = new MoveTo(xPos, yPos);
            path.getElements().add(moveTo);

            for (int i = 0; i < listSchleife.size(); i += 1) {
                EleTimePOJO eleTimePOJO = listSchleife.get(i);

                xPos = offsetX + (eleTimePOJO.getAzimuth() * (getWidth() - 2 * offsetX) / (double) 360);
                yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (eleTimePOJO.getAltitude() / roundUp));

                //System.out.println("i "+i+" xpos"+xPos+" "+listSchleife.get(listSchleife.size()-1).getAzimuth()+" azi "+eleTimePOJO.getAzimuth()+" alti "+eleTimePOJO.getAltitude());
                if (eleTimePOJO.getAltitude() < 0) {
                    yPos = getHeight() - offsetY;
                }

                LocalDateTime localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

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
                float color[] = SunChartPolarAnchorPage.generateAdvancedColor(i / (float) listSchleife.size());
                Circle circle = new Circle(xPos, yPos, 3.0f, Color.rgb((int) (color[0] * 255), (int) (color[1] * 255), (int) (color[2] * 255)));
                getChildren().add(circle);
                */

                LineTo lineTo = new LineTo(xPos, yPos);
                path.getElements().add(lineTo);
            }
            getChildren().add(path);
        }
    }

    private void drawSonnenStandLines() {

        for (Integer hour : mapSonnenStand.keySet()) {

            Path path = new Path();
            path.setStroke(Color.RED);
            path.setStrokeWidth(2.0f);

            if (hour > 1) {
                path.setStroke(Color.BLACK);
                path.setStrokeWidth(0.5f);
            }

            List<EleTimePOJO> listSonnenStand = mapSonnenStand.get(hour);

            //First Element
            double xPos = offsetX + (listSonnenStand.get(0).getAzimuth() * (getWidth() - 2 * offsetX) / (double) 360);
            double yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (listSonnenStand.get(0).getAltitude() / roundUp));

            if (listSonnenStand.get(0).getAltitude() < 0) {
                yPos = getHeight() - offsetY;
            }

            MoveTo moveTo = new MoveTo(xPos, yPos);
            path.getElements().add(moveTo);

            for (int i = 0; i < listSonnenStand.size(); i += 1) {
                EleTimePOJO eleTimePOJO = listSonnenStand.get(i);

                //System.out.println("ele "+eleTimePOJO.getAzimuth()+" "+eleTimePOJO.getAltitude());
                xPos = offsetX + (eleTimePOJO.getAzimuth() * (getWidth() - 2 * offsetX) / (double) 360);
                yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (eleTimePOJO.getAltitude() / roundUp));

                if (eleTimePOJO.getAltitude() < 0) {
                    yPos = getHeight() - offsetY;
                }

                LocalDateTime localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

                for (int k = 0; k < 24; k++) {

                    if (hour == 0) {
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
            getChildren().add(path);
        }
    }

    private void drawSonnenStand() {

        for (Integer hour : mapSonnenStand.keySet()) {

            Path path = new Path();
            path.setStroke(Color.RED);
            path.setStrokeWidth(2.0f);

            if (hour > 1) {
                path.setStroke(Color.BLACK);
                path.setStrokeWidth(0.5f);
            }

            List<EleTimePOJO> listSonnenStand = mapSonnenStand.get(hour);

            //First Element
            double xPos = offsetX + (listSonnenStand.get(0).getAzimuth() * (getWidth() - 2 * offsetX) / (double) 360);
            double yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (listSonnenStand.get(0).getAltitude() / roundUp));

            if (listSonnenStand.get(0).getAltitude() < 0) {
                yPos = getHeight() - offsetY;
            }

            MoveTo moveTo = new MoveTo(xPos, yPos);
            path.getElements().add(moveTo);

            for (int i = 0; i < listSonnenStand.size(); i += 1) {
                EleTimePOJO eleTimePOJO = listSonnenStand.get(i);

                //System.out.println("ele "+eleTimePOJO.getAzimuth()+" "+eleTimePOJO.getAltitude());
                xPos = offsetX + (eleTimePOJO.getAzimuth() * (getWidth() - 2 * offsetX) / (double) 360);
                yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (eleTimePOJO.getAltitude() / roundUp));

                if (eleTimePOJO.getAltitude() < 0) {
                    yPos = getHeight() - offsetY;
                }

                LocalDateTime localDateTime = eleTimePOJO.getLocalDateTime().minusHours(1);

                if (hour == 0) {
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
            getChildren().add(path);
        }
    }

    private void clear() {
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.WHITE);
        getChildren().clear();
        getChildren().add(rectangle);
    }

    public void setMapSchleifen(HashMap<Integer, List<EleTimePOJO>> mapSchleifen) {
        this.mapSchleifen = mapSchleifen;
    }

    public void setMapSonnenStand(HashMap<Integer, List<EleTimePOJO>> mapSonnenStand) {
        this.mapSonnenStand = mapSonnenStand;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
