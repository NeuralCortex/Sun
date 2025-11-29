package com.fx.sun.controller.cell;

import com.fx.sun.pojo.DatePOJO;
import com.fx.sun.tools.MoonAnchorPage;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.commons.text.StringEscapeUtils;
import org.shredzone.commons.suncalc.MoonIllumination;
import org.shredzone.commons.suncalc.MoonPhase;

/**
 *
 * @author pscha
 */
public class MoonCell extends TableCell<DatePOJO, LocalDate> {

    private final LocalDate now = LocalDate.now();
    private LocalDate month = null;
    private final Image moon;
    private final ResourceBundle bundle;
    private boolean isNight = false;

    public MoonCell(LocalDate month, Image moon, ResourceBundle bundle, boolean isNight) {
        this.month = month;
        this.moon = moon;
        this.bundle = bundle;
        this.isNight = isNight;
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
            VBox vBoxInfo = new VBox();
           
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefSize(40, 40);

            Label datum = new Label(simpleDateFormat.format(calendar.getTime()));
          
            if (isNight) {
                
            }
            if (item.equals(now)) {
                datum.getStyleClass().add("text-red");
            }

            MoonIllumination moonIllumination = MoonIllumination.compute().on(item).execute();

            /*
            MoonCanvas moonCanvas = new MoonCanvas(moon, moonIllumination.getPhase());
            moonCanvas.widthProperty().bind(anchorPane.widthProperty());
            moonCanvas.heightProperty().bind(anchorPane.heightProperty());
            anchorPane.getChildren().add(moonCanvas);
             */
            //System.out.println("date: "+item+ "angle "+moonIllumination.getAngle());
            double angle = moonIllumination.getAngle();
            if (angle < 0) {
                angle += 90;
            } else {
                angle -= 90;
            }
            MoonAnchorPage moonAnchorPage = new MoonAnchorPage(moon, moonIllumination.getPhase(), angle, isNight);
            moonAnchorPage.setPrefSize(40, 40);

            vBox2.getChildren().addAll(moonAnchorPage);

            hBox.setAlignment(Pos.CENTER_LEFT);

            Label lbPhase = new Label(getPhaseName(moonIllumination.getClosestPhase()));
            if (isNight) {
               
            }

            MoonPhase.Parameters parameters = MoonPhase.compute().phase(MoonPhase.Phase.FULL_MOON);
            MoonPhase moonPhase = parameters.on(date).execute();
            String moonKind = "";
            if (moonPhase.isMicroMoon()) {
                moonKind = bundle.getString("moon.micro");
            }
            if (moonPhase.isSuperMoon()) {
                moonKind = bundle.getString("moon.super");
            }
            Label lbKind = new Label(moonKind);
            lbKind.getStyleClass().add("text-red");

            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            Label lbLit = new Label(bundle.getString("moon.lit") + ":\n" + (decimalFormat.format(moonIllumination.getFraction() * 100.0f)) + "%");
            if (isNight) {
               
            }

            vBoxInfo.getChildren().addAll(lbPhase, lbKind, lbLit);
            hBox.setSpacing(10);
            hBox.getChildren().addAll(vBox2, vBoxInfo);

            vBox.getChildren().addAll(datum, hBox);

            VBox.setMargin(datum, new Insets(10));
            VBox.setMargin(hBox, new Insets(0, 10, 10, 10));

            if (isNight) {
                vBox.getStyleClass().add("black");
            }
            if (item.getMonth().equals(month.getMonth())) {
                //vBox.getStyleClass().add("orange");
                moonAnchorPage.setBackGroundColor(Color.web("#f96609"));
                if (isNight) {
                    vBox.getStyleClass().add("black");
                }
            } else {
                moonAnchorPage.setBackGroundColor(Color.web("#3c3f41"));
            }

            moonAnchorPage.redraw();

            setGraphic(vBox);
            setText(null);
        }
    }

    private String getPhaseName(MoonPhase.Phase moonPhase) {
        String phaseName = null;
        switch (moonPhase) {
            case FIRST_QUARTER:
                phaseName = StringEscapeUtils.unescapeJava(bundle.getString("phase.first"));
                break;
            case FULL_MOON:
                phaseName = StringEscapeUtils.unescapeJava(bundle.getString("phase.full"));
                break;
            case LAST_QUARTER:
                phaseName = StringEscapeUtils.unescapeJava(bundle.getString("phase.last"));
                break;
            case NEW_MOON:
                phaseName = StringEscapeUtils.unescapeJava(bundle.getString("phase.new"));
                break;
            case WANING_CRESCENT:
                phaseName = StringEscapeUtils.unescapeJava(bundle.getString("phase.wan.cres"));
                break;
            case WANING_GIBBOUS:
                phaseName = StringEscapeUtils.unescapeJava(bundle.getString("phase.wan.gibb"));
                break;
            case WAXING_CRESCENT:
                phaseName = StringEscapeUtils.unescapeJava(bundle.getString("phase.wax.cres"));
                break;
            case WAXING_GIBBOUS:
                phaseName = StringEscapeUtils.unescapeJava(bundle.getString("phase.wax.gibb"));
                break;
            default:

        }
        return phaseName;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }
}
