package com.fx.sun.pojo;

/**
 *
 * @author pscha
 */
public class TimePOJO {

    private int pos;
    private double time;

    public TimePOJO(){
        
    }
    
    public TimePOJO(int pos, double time) {
        this.pos = pos;
        this.time = time;
    }
    
    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
