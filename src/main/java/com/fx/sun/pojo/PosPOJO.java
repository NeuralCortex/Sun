package com.fx.sun.pojo;

/**
 *
 * @author pscha
 */
public class PosPOJO {

    private double lat;
    private double lon;

    public PosPOJO(){
        
    }
    
    public PosPOJO(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "PosPOJO{" + "lat=" + lat + ", lon=" + lon + '}';
    }
}
