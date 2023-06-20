package com.fx.sun.pojo;

import java.time.LocalDate;

/**
 *
 * @author pscha
 */
public class DatePOJO {

    private LocalDate monday=null;
    private LocalDate tuesday=null;
    private LocalDate wednesday=null;
    private LocalDate thursday=null;
    private LocalDate friday=null;
    private LocalDate saturday=null;
    private LocalDate sunday=null;

    public DatePOJO(){
        
    }
    
    public DatePOJO(LocalDate monday, LocalDate tuesday, LocalDate wednesday, LocalDate thursday, LocalDate friday, LocalDate saturday, LocalDate sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public LocalDate getMonday() {
        return monday;
    }

    public void setMonday(LocalDate monday) {
        this.monday = monday;
    }

    public LocalDate getTuesday() {
        return tuesday;
    }

    public void setTuesday(LocalDate tuesday) {
        this.tuesday = tuesday;
    }

    public LocalDate getWednesday() {
        return wednesday;
    }

    public void setWednesday(LocalDate wednesday) {
        this.wednesday = wednesday;
    }

    public LocalDate getThursday() {
        return thursday;
    }

    public void setThursday(LocalDate thursday) {
        this.thursday = thursday;
    }

    public LocalDate getFriday() {
        return friday;
    }

    public void setFriday(LocalDate friday) {
        this.friday = friday;
    }

    public LocalDate getSaturday() {
        return saturday;
    }

    public void setSaturday(LocalDate saturday) {
        this.saturday = saturday;
    }

    public LocalDate getSunday() {
        return sunday;
    }

    public void setSunday(LocalDate sunday) {
        this.sunday = sunday;
    }

    @Override
    public String toString() {
        return "DatePOJO{" + "monday=" + monday + ", tuesday=" + tuesday + ", wednesday=" + wednesday + ", thursday=" + thursday + ", friday=" + friday + ", saturday=" + saturday + ", sunday=" + sunday + '}';
    }
}

