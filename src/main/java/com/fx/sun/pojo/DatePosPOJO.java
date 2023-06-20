package com.fx.sun.pojo;

import java.time.LocalDate;

/**
 *
 * @author pscha
 */
public class DatePosPOJO {

    private int pos;
    private LocalDate date;

    public DatePosPOJO(){
        
    }
    
    public DatePosPOJO(int pos, LocalDate date) {
        this.pos = pos;
        this.date = date;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DatePosPOJO{" + "pos=" + pos + ", date=" + date + '}';
    }
}
