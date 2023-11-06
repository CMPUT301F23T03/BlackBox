package com.example.blackbox;


/**
 * This class represents a color object
 */
public class TagColor {
    private String name;
    private Integer color;
    public TagColor(String name, Integer color){
        this.name = name;
        this.color = color;
    }
    public String getName(){
        return name;
    }
    public Integer getColor(){
        return color;
    }
}
