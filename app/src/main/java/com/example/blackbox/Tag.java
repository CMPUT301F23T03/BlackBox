package com.example.blackbox;

import java.util.ArrayList;

public class Tag {
    private String name;
    private String color;
    /**
     * Constructor of an Tag object
     *
     * @param  name  the name of the tag
     */
    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
