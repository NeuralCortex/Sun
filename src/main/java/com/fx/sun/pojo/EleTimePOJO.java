package com.fx.sun.pojo;

import java.time.LocalDateTime;

/**
 *
 * @author pscha
 */
public class EleTimePOJO {
    
    private LocalDateTime localDateTime;
    private double time;
    private double azimuth;
    private double altitude;

    public EleTimePOJO(double time, double azimuth, double altitude) {
        this.time = time;
        this.azimuth = azimuth;
        this.altitude = altitude;
    }
    
    public EleTimePOJO(double time, double azimuth, double altitude,LocalDateTime localDateTime) {
        this.time = time;
        this.azimuth = azimuth;
        this.altitude = altitude;
        this.localDateTime=localDateTime;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "EleTimePOJO{" + "localDateTime=" + localDateTime + ", time=" + time + ", azimuth=" + azimuth + ", altitude=" + altitude + '}';
    }
}
