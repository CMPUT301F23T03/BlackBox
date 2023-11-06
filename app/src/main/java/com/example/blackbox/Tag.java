package com.example.blackbox;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable {
    private String name;
    private String description;
    private int color;
    private String colorName;

    private String dataBaseID;
    /**
     * Constructor of an Tag object
     *
     * @param  name  the name of the tag
     */
    public Tag(String name, int color, String colorName) {
        this.name = name;
        this.color = color;
        this.colorName = colorName;
    }
    public Tag(String name, int color, String colorName, String description) {
        this(name,color,colorName);
        this.description = description;
    }
    public Tag(String name, int color, String colorName, String description, String dataBaseID) {
        this(name,color,colorName, description);
        this.dataBaseID = dataBaseID;
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

    public String getColorName() {
        return colorName;
    }

    public String getDataBaseID() {
        return dataBaseID;
    }
}
