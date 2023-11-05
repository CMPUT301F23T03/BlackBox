package com.example.blackbox;

import java.util.ArrayList;

public class Tag {
    private String name;
    private String description;
    private int color;
    /**
     * Constructor of an Tag object
     *
     * @param  name  the name of the tag
     */
    public Tag(String name, int color) {
        this.name = name;
        this.color = color;
        this.description = "";
    }
    public Tag(String name, int color, String description) {
        this(name,color);
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getHexStringColor(){
        return String.format("#%02x", color);
    }
    public int getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }
}
