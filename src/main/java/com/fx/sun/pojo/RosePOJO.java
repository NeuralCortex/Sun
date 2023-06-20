package com.fx.sun.pojo;

/**
 *
 * @author pscha
 */
public class RosePOJO {

    private double id;
    private String name;

    public RosePOJO(double id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
