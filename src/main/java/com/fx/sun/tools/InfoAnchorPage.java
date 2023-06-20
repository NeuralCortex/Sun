package com.fx.sun.tools;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.shredzone.commons.suncalc.SunTimes;

/**
 *
 * @author pscha
 */
public class InfoAnchorPage extends AnchorPane {

    private SunTimes.Parameters sunTimes;
    private double radius;
    private final Font font = new Font("Arial", 10.0f);

    private enum DESC {
        VISUAL, SUNSET, CIVIL, NAUTICAL, ASTRONOMICAL,
        GOLDEN_HOUR, BLUE_HOUR, NIGHT_HOUR, NOON, NADIR
    };

    public InfoAnchorPage() {
        setMinSize(0, 0);
        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public void redraw() {
        clear();

        radius = getHeight() / 2.0f - 100;
        if (getWidth() < getHeight()) {
            radius = getWidth() / 2.0f - 200;
        }

        SunTimes visual = sunTimes.copy().twilight(SunTimes.Twilight.VISUAL).execute();
        SunTimes civil = sunTimes.copy().twilight(SunTimes.Twilight.CIVIL).execute();
        SunTimes nautical = sunTimes.copy().twilight(SunTimes.Twilight.NAUTICAL).execute();
        SunTimes astro = sunTimes.copy().twilight(SunTimes.Twilight.ASTRONOMICAL).execute();
        SunTimes blue = sunTimes.copy().twilight(SunTimes.Twilight.BLUE_HOUR).execute();
        SunTimes night = sunTimes.copy().twilight(SunTimes.Twilight.NIGHT_HOUR).execute();
        SunTimes golden = sunTimes.copy().twilight(SunTimes.Twilight.GOLDEN_HOUR).execute();

        drawCircle();

        drawArc(getTimeDecimal(visual.getRise()), getTimeDecimal(visual.getSet()), radius, Color.YELLOW);

        drawArc(getTimeDecimal(astro.getSet()), getTimeDecimal(astro.getRise()), radius, Color.BLACK);

        drawArc(getTimeDecimal(civil.getRise()), getTimeDecimal(visual.getRise()), radius, Color.LIGHTGRAY);
        drawArc(getTimeDecimal(visual.getSet()), getTimeDecimal(civil.getSet()), radius, Color.LIGHTGRAY);

        drawArc(getTimeDecimal(nautical.getRise()), getTimeDecimal(civil.getRise()), radius, Color.GRAY);
        drawArc(getTimeDecimal(civil.getSet()), getTimeDecimal(nautical.getSet()), radius, Color.GRAY);

        drawArc(getTimeDecimal(astro.getRise()), getTimeDecimal(nautical.getRise()), radius, Color.DARKGRAY);
        drawArc(getTimeDecimal(nautical.getSet()), getTimeDecimal(astro.getSet()), radius, Color.DARKGREY);

        //Blue Hour
        drawArc(getTimeDecimal(night.getRise()), getTimeDecimal(blue.getRise()), radius - 40, Color.BLUE);
        drawArc(getTimeDecimal(blue.getSet()), getTimeDecimal(night.getSet()), radius - 40, Color.BLUE);

        //Golden Hour
        drawArc(getTimeDecimal(blue.getRise()), getTimeDecimal(golden.getRise()), radius - 80, Color.ORANGE);
        drawArc(getTimeDecimal(golden.getSet()), getTimeDecimal(blue.getSet()), radius - 80, Color.ORANGE);

        drawCoordSys();

        //Rechts
        drawLine(getTimeDecimal(visual.getSet()), Color.BLACK, 1.0f, 40.0f);
        drawLine(getTimeDecimal(visual.getNoon()), Color.BLACK, 1.0f, 40.0f);
        drawLine(getTimeDecimal(visual.getNadir()), Color.BLACK, 1.0f, 40.0f);
        drawLine(getTimeDecimal(golden.getSet()), Color.BLACK, 1.0f, 40.0f);
        drawLine(getTimeDecimal(blue.getSet()), Color.BLACK, 1.0f, 40.0f);
        drawLine(getTimeDecimal(night.getSet()), Color.BLACK, 1.0f, 40.0f);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        drawDesc(getTimeDecimal(visual.getSet()), Color.BLACK, 45.0f, DESC.SUNSET.name() + ": " + visual.getSet().format(dateTimeFormatter));
        drawDesc(getTimeDecimal(golden.getSet()), Color.BLACK, 45.0f, DESC.GOLDEN_HOUR.name() + ": " + golden.getSet().format(dateTimeFormatter));
        drawDesc(getTimeDecimal(blue.getSet()), Color.BLACK, 45.0f, DESC.BLUE_HOUR.name() + ": " + blue.getSet().format(dateTimeFormatter));
        drawDesc(getTimeDecimal(night.getSet()), Color.BLACK, 45.0f, DESC.NIGHT_HOUR.name() + ": " + night.getSet().format(dateTimeFormatter));

        //Links
        drawLine(getTimeDecimal(visual.getRise()), Color.BLACK, 1.0f, 40.0f);
        drawLine(getTimeDecimal(civil.getRise()), Color.BLACK, 1.0f, 40.0f);
        drawLine(getTimeDecimal(nautical.getRise()), Color.BLACK, 1.0f, 40.0f);
        drawLine(getTimeDecimal(astro.getRise()), Color.BLACK, 1.0f, 40.0f);

        drawDesc(getTimeDecimal(visual.getRise()), Color.BLACK, 45.0f, DESC.VISUAL.name() + ": " + visual.getRise().format(dateTimeFormatter));
        drawDesc(getTimeDecimal(visual.getNoon()), Color.BLACK, 45.0f, DESC.NOON.name() + ": " + visual.getNoon().format(dateTimeFormatter));
        drawDesc(getTimeDecimal(visual.getNadir()), Color.BLACK, 45.0f, DESC.NADIR.name() + ": " + visual.getNadir().format(dateTimeFormatter));
        drawDesc(getTimeDecimal(civil.getRise()), Color.BLACK, 45.0f, DESC.CIVIL.name() + ": " + civil.getRise().format(dateTimeFormatter));
        drawDesc(getTimeDecimal(nautical.getRise()), Color.BLACK, 45.0f, DESC.NAUTICAL.name() + ": " + nautical.getRise().format(dateTimeFormatter));
        drawDesc(getTimeDecimal(astro.getRise()), Color.BLACK, 45.0f, DESC.ASTRONOMICAL.name() + ": " + astro.getRise().format(dateTimeFormatter));

        //Beschreibung
        double offsetX = 120.0f;
        double offsetY = 10.0f;

        Duration diffDay = Duration.between(visual.getRise(), visual.getSet());
        Duration diffNight = Duration.between(astro.getRise(), astro.getSet()).minusHours(24);

        drawRectDesc(getWidth() - offsetX, offsetY, Color.YELLOW, "Day: " + getTimeDiff(diffDay));
        drawRectDesc(getWidth() - offsetX, offsetY += 25, Color.ORANGE, "Golden hour");
        drawRectDesc(getWidth() - offsetX, offsetY += 25, Color.LIGHTGREY, "Civil");
        drawRectDesc(getWidth() - offsetX, offsetY += 25, Color.BLUE, "Blue hour");
        drawRectDesc(getWidth() - offsetX, offsetY += 25, Color.GRAY, "Nautical");
        drawRectDesc(getWidth() - offsetX, offsetY += 25, Color.DARKGRAY, "Astronomical");
        drawRectDesc(getWidth() - offsetX, offsetY += 25, Color.BLACK, "Night: " + getTimeDiff(diffNight));
    }

    public static String getTimeDiff(Duration duration) {
        int hours = Math.abs(duration.toHoursPart());
        int min = Math.abs(duration.toMinutesPart());
        int sec = Math.abs(duration.toSecondsPart());
        return String.format("%02d:%02d:%02d", hours, min, sec);
    }

    private double getTimeDecimal(ZonedDateTime date) {
        double min = date.getMinute() / 60.0f;
        double sec = date.getSecond() / 3600.0f;
        return date.getHour() + min + sec;
    }

    private void clear() {
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.WHITE);
        getChildren().clear();
        getChildren().add(rectangle);
    }

    private void drawCircle() {
        double centerX = getWidth() / 2.0f;
        double centerY = getHeight() / 2.0f;

        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2.0f);
        getChildren().addAll(circle);
    }

    private void drawCoordSys() {
        for (int i = 0; i < 24; i++) {
            drawLine(i, Color.DARKGOLDENROD, 2.0f, 0.0f);
            drawTime(i, Color.BLACK);
        }
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

    private void drawLine(double time, Color color, double width, double length) {
        double centerX = getWidth() / 2.0f;
        double centerY = getHeight() / 2.0f;
        double t = Math.toRadians(((360.0f / 24.0f) * time) + 90);
        double endX = centerX + (length + radius) * Math.cos(t);
        double endY = centerY + (length + radius) * Math.sin(t);
        Line line = new Line(centerX, centerY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(width);
        getChildren().add(line);
    }

    private void drawDesc(double time, Color color, double length, String desc) {
        double centerX = getWidth() / 2.0f;
        double centerY = getHeight() / 2.0f;
        double t = Math.toRadians(((360.0f / 24.0f) * time) + 90);
        double x = length;
        double y = length;
        Text text = new Text(desc);
        text.setFont(font);
        Bounds bounds = text.getLayoutBounds();
        double endX = centerX + (x + radius) * Math.cos(t);
        double endY = centerY + (y + radius) * Math.sin(t);
        if (desc.contains(DESC.VISUAL.name()) || desc.contains(DESC.CIVIL.name()) || desc.contains(DESC.NAUTICAL.name()) || desc.contains(DESC.ASTRONOMICAL.name())) {
            text.setX(endX - bounds.getWidth());
            //Circle circle=new Circle(endX, endY, 3, Color.BLUE);
            //getChildren().add(circle);
        } else {
            text.setX(endX);
        }
        text.setY(endY);
        if (desc.contains(DESC.NADIR.name())) {
            text.setY(endY - bounds.getMinY());
        }
        getChildren().add(text);
    }

    private void drawTime(double time, Color color) {
        double centerX = getWidth() / 2.0f;
        double centerY = getHeight() / 2.0f;
        double t = Math.toRadians(((360.0f / 24.0f) * time) + 90);
        Text text = new Text(time + "");
        text.setFont(font);
        if (time == 0) {
            text = new Text("24.0");
            text.setFont(font);
        }
        Bounds bounds = text.getLayoutBounds();

        double x = 0, y = 0;
        double offset = 5.0f;

        if (time <= 6.0f) {
            x = bounds.getWidth();
            y = bounds.getHeight();
        }
        if (time > 6.0f && time <= 12.0f) {
            x = bounds.getWidth();
            y = bounds.getMaxY();
        }
        if (time > 12.0f && time <= 18.0f) {
            x = bounds.getMinX();
            y = -bounds.getMaxY();
        }
        if (time > 18.0f) {
            x = bounds.getMinX();
            y = bounds.getHeight();
        }

        x = x + offset;
        y = y + offset;

        double endX = centerX + (x + radius) * Math.cos(t);
        double endY = centerY + (y + radius) * Math.sin(t);
        text.setX(endX);
        text.setY(endY);
        text.setFill(color);
        getChildren().add(text);
    }

    private void drawArc(double start, double end, double radius, Color color) {
        //System.out.println("s "+start+" e"+end);
        double centerX = getWidth() / 2.0f;
        double centerY = getHeight() / 2.0f;
        double angleStart = 270.0f - ((360.0f / 24.0f) * start);
        double angleEnd = 270.0f - ((360.0f / 24.0f) * end);
        double length = angleEnd - angleStart;
        Arc arc = new Arc(centerX, centerY, radius, radius, angleStart, length);
        if (start > end) {
            //angleStart = ((360.0f / 24.0f) * start);
            angleEnd = 270 - ((360.0f / 24.0f) * end);
            length = 360 - length;
            arc = new Arc(centerX, centerY, radius, radius, angleEnd, length);
        }
        //System.out.println("s "+angleStart+" e "+angleEnd+" l "+length);
        arc.setFill(color);
        arc.setType(ArcType.ROUND);
        getChildren().add(arc);
    }

    public void setSunTimes(SunTimes.Parameters sunTimes) {
        this.sunTimes = sunTimes;
    }
}
