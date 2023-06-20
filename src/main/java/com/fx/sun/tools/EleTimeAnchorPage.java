package com.fx.sun.tools;

import com.fx.sun.pojo.ComboBoxPOJO;
import com.fx.sun.pojo.EleTimePOJO;
import java.util.ArrayList;
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
public class EleTimeAnchorPage extends AnchorPane {

    private int offsetX = 70;
    private int offsetY = 30;
    private List<EleTimePOJO> listSun;
    private List<EleTimePOJO> listMoon;
    private double maxMoon;
    private double maxSun;
    double max = 0;
    double roundUp = 0;
    int stepping = 5;

    private Font font = new Font("Arial", 10.0f);

    public EleTimeAnchorPage() {
        setMinSize(0, 0);

        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public void redraw() {
        clear();

        drawCoordSys();
        if (listSun != null && listMoon != null) {
            drawSun();
            drawMoon();
        }
    }

    private void drawCoordSys() {

        for (int i = 0; i <= 24; i++) {
            double x0 = offsetX + (getWidth() - 2 * offsetX) * i / 24.0f;
            double y0 = offsetY;
            double x1 = x0;
            double y1 = offsetY + (getHeight() - 2 * offsetY);
            Line line = new Line(x0, y0, x1, y1);
            line.setStrokeWidth(1.0f);
            getChildren().add(line);

            String time = String.format("%02d", i) + ":" + String.format("%02d", 0);
            Text text = new Text(time);
            Bounds bounds = text.getLayoutBounds();
            text.setFont(font);
            text.setX(offsetX + (getWidth() - 2 * offsetX) * (i / 24.0f));
            text.setY(getHeight() - offsetY - bounds.getMinY());
            getChildren().add(text);
        }

        for (int i = 0; i <= 24; i += 3) {
            double x0 = offsetX + (getWidth() - 2 * offsetX) * i / 24.0f;
            double y0 = offsetY;
            double x1 = x0;
            double y1 = offsetY + (getHeight() - 2 * offsetY);
            Line line = new Line(x0, y0, x1, y1);
            line.setStrokeWidth(4.0f);
            getChildren().add(line);
        }

        ArrayList<ComboBoxPOJO> listSunDesc = new ArrayList();
        listSunDesc.add(new ComboBoxPOJO(0, "N"));
        listSunDesc.add(new ComboBoxPOJO(6, "E"));
        listSunDesc.add(new ComboBoxPOJO(12, "S"));
        listSunDesc.add(new ComboBoxPOJO(18, "W"));

        for (int j = 0; j < listSunDesc.size(); j++) {
            ComboBoxPOJO comboBoxPOJO = listSunDesc.get(j);

            Text text = new Text(comboBoxPOJO.getName());
            Bounds bounds = text.getLayoutBounds();
            text.setFont(font);
            text.setFill(Color.RED);
            text.setX(offsetX + (getWidth() - 2 * offsetX) * (comboBoxPOJO.getId() / 24.0f));
            text.setY(offsetY - bounds.getMaxY());
            getChildren().add(text);
        }

        ArrayList<ComboBoxPOJO> listMoonDesc = new ArrayList();
        listMoonDesc.add(new ComboBoxPOJO(3, "S"));
        listMoonDesc.add(new ComboBoxPOJO(9, "W"));
        listMoonDesc.add(new ComboBoxPOJO(15, "N"));
        listMoonDesc.add(new ComboBoxPOJO(21, "E"));

        for (int j = 0; j < listMoonDesc.size(); j++) {
            ComboBoxPOJO comboBoxPOJO = listMoonDesc.get(j);

            Text text = new Text(comboBoxPOJO.getName());
            Bounds bounds = text.getLayoutBounds();
            text.setFont(font);
            text.setFill(Color.BLUE);
            text.setX(offsetX + (getWidth() - 2 * offsetX) * (comboBoxPOJO.getId() / 24.0f));
            text.setY(offsetY - bounds.getMaxY());
            getChildren().add(text);
        }

        if (maxSun > maxMoon) {
            max = maxSun;
        } else {
            max = maxMoon;
        }

        roundUp = (int) (Math.ceil(max / 10.0f) * 10.0f);

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
            text.setX(offsetX - bounds.getMaxX());
            text.setY(offsetY + (getHeight() - 2 * offsetY) * (i / roundUp));
            getChildren().add(text);
        }

        drawRectDesc(getWidth() - offsetX + 10, offsetY, Color.RED, "Sun");
        drawRectDesc(getWidth() - offsetX + 10, offsetY + 25, Color.BLUE, "Moon");
    }

    private void drawRectDesc(double x, double y, Color color, String desc) {
        double width = 20.0f;
        double height = 20.0f;
        double offset = 5.0f;

        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(color);

        Text text = new Text(desc);
        text.setFont(font);
        //Bounds bounds=text.getLayoutBounds();
        text.setX(x + width + offset);
        text.setY(y + height - 5.0f);

        getChildren().addAll(rectangle, text);
    }

    private void drawSun() {
        Path path = new Path();
        path.setStroke(Color.RED);
        path.setStrokeWidth(2.0f);

        //First Element
        double xPos = offsetX;
        double yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (listSun.get(0).getAltitude() / roundUp));

        if (listSun.get(0).getAltitude() < 0) {
            yPos = getHeight() - offsetY;
        }

        MoveTo moveTo = new MoveTo(xPos, yPos);
        path.getElements().add(moveTo);

        for (int i = 0; i < listSun.size(); i++) {
            EleTimePOJO eleTimePOJO = listSun.get(i);

            xPos = offsetX + (eleTimePOJO.getTime()) * ((getWidth() - 2 * offsetX) / 24.0f);
            yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (eleTimePOJO.getAltitude() / roundUp));

            if (eleTimePOJO.getAltitude() < 0) {
                yPos = getHeight() - offsetY;
            }

            LineTo lineTo = new LineTo(xPos, yPos);
            path.getElements().add(lineTo);
        }

        getChildren().add(path);
    }

    private void drawMoon() {
        Path path = new Path();
        path.setStroke(Color.BLUE);
        path.setStrokeWidth(2.0f);

        //First Element
        double xPos = offsetX;
        double yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (listMoon.get(0).getAltitude() / roundUp));

        if (listMoon.get(0).getAltitude() < 0) {
            yPos = getHeight() - offsetY;
        }

        MoveTo moveTo = new MoveTo(xPos, yPos);
        path.getElements().add(moveTo);

        for (int i = 0; i < listMoon.size(); i++) {
            EleTimePOJO eleTimePOJO = listMoon.get(i);

            xPos = offsetX + (eleTimePOJO.getTime()) * ((getWidth() - 2 * offsetX) / 24.0f);
            yPos = (getHeight()) - (offsetY + (getHeight() - 2 * offsetY) * (eleTimePOJO.getAltitude() / roundUp));

            if (eleTimePOJO.getAltitude() < 0) {
                yPos = getHeight() - offsetY;
            }

            LineTo lineTo = new LineTo(xPos, yPos);
            path.getElements().add(lineTo);
        }

        getChildren().add(path);
    }

    private void clear() {
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.WHITE);
        getChildren().clear();
        getChildren().add(rectangle);
    }

    public void setListSun(List<EleTimePOJO> listSun) {
        this.listSun = listSun;
    }

    public void setListMoon(List<EleTimePOJO> listMoon) {
        this.listMoon = listMoon;
    }

    public void setMaxMoon(double maxMoon) {
        this.maxMoon = maxMoon;
    }

    public void setMaxSun(double maxSun) {
        this.maxSun = maxSun;
    }
}
