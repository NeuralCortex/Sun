package com.fx.sun.pojo;

/**
 *
 * @author pscha
 */
public class ComboBoxPOJO {

    private int id;
    private String name;

    public ComboBoxPOJO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
