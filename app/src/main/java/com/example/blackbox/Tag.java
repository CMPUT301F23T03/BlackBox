package com.example.blackbox;

import java.util.ArrayList;

public class Tag {
    private String name;
    private int color;
    /**
     * Constructor of an Tag object
     *
     * @param  name  the name of the tag
     */
    public Tag(String name, int color) {
        this.name = name;
        this.color = color;
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
}
