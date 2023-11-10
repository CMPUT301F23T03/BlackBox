package com.example.blackbox;


/**
 * This class represents an object which is displayed by a ColorSpinnerAdapter
 * specifically these represent colors that can be chosen for tags
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
