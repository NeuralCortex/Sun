package com.fx.sun.tools;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

/**
 *
 * @author pscha
 */
public class MoonAnchorPage extends AnchorPane {

    private final Image moon;
    private double rotAngle;
    private double phase = 30;
    private int size = 200;
    private boolean isNight = false;
    private Color backGroundColor;

    public MoonAnchorPage(Image moon, double phase, double rotAngle, boolean isNight) {
        this.moon = moon;
        this.phase = phase;
        this.rotAngle = rotAngle;
        this.isNight = isNight;

        setMinSize(0, 0);

        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public void redraw() {
        clear();

        double centerX = getWidth() / 2.0f;
        double centerY = getHeight() / 2.0f;

        ImageView imageView = new ImageView(moon);
        imageView.setX(centerX - moon.getWidth() / 2.0f);
        imageView.setY(centerY - moon.getHeight() / 2.0f);
        getChildren().add(imageView);

        double radius = moon.getWidth() / 2.0f;

        double xm = centerX;
        double ym = centerY;
        int orient = 270;
        if (phase < 0) {
            orient = 90;
        }

        Path path = new Path();
        path.setFill(backGroundColor);
        path.setStroke(backGroundColor);
        if (isNight) {
            path.setFill(Color.BLACK);
            path.setStroke(Color.BLACK);
        }
        for (int i = 0; i < 180; i++) {
            double x = xm + radius * Math.cos(Math.toRadians(i + orient));
            double y = ym + radius * Math.sin(Math.toRadians(i + orient));
            if (i == 0) {
                MoveTo moveTo = new MoveTo(x, y);
                path.getElements().add(moveTo);
            } else {
                LineTo lineTo = new LineTo(x, y);
                path.getElements().add(lineTo);
            }
        }
        for (int i = 180; i >= 0; i--) {
            double sf = radius - (radius * (Math.abs(phase) / 90.0f));
            double x = xm + sf * Math.cos(Math.toRadians(i + orient));

            if (phase > 0) {
                sf = (radius * (Math.abs(phase) / 90.0f)) - radius;
                x = xm - sf * Math.cos(Math.toRadians(i + orient));
            }

            double y = ym + radius * Math.sin(Math.toRadians(i + orient));
            LineTo lineTo = new LineTo(x, y);
            path.getElements().add(lineTo);
        }
        path.getElements().add(new ClosePath());
        getChildren().add(path);

        //imageView.setRotate(rotAngle);
        path.getTransforms().add(Affine.rotate(rotAngle, xm, ym));
    }

    private void clear() {
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.TRANSPARENT);
        if(isNight){
            rectangle.setFill(Color.BLACK);
        }
        getChildren().clear();
        getChildren().add(rectangle);
    }

    public void setRotAngle(double rotAngle) {
        this.rotAngle = rotAngle;
    }

    public void setBackGroundColor(Color backGroundColor) {
        this.backGroundColor = backGroundColor;
    }
}
