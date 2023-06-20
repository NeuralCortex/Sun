package com.fx.sun.pojo;

/**
 *
 * @author pscha
 */
public class TablePOJO {

    private String date;
    private String time;
    private Double azimuth;
    private Double altitude;
    private String distance;
    private Double parallactic;
    private Double truealti;

    public TablePOJO() {

    }

    public TablePOJO(String date, String time, Double azimuth, Double altitude, String distance, Double parallactic, Double truealti) {
        this.date = date;
        this.time = time;
        this.azimuth = azimuth;
        this.altitude = altitude;
        this.distance = distance;
        this.parallactic = parallactic;
        this.truealti = truealti;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(Double azimuth) {
        this.azimuth = azimuth;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Double getParallactic() {
        return parallactic;
    }

    public void setParallactic(Double parallactic) {
        this.parallactic = parallactic;
    }

    public Double getTruealti() {
        return truealti;
    }

    public void setTruealti(Double truealti) {
        this.truealti = truealti;
    }
}
