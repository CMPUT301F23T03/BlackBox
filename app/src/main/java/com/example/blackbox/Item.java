package com.example.blackbox;

import android.media.Image;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class Item implements Serializable {

    // initialize data members
    // we might add a data member of reference to an image later on
    private String name;
    private ArrayList<Tag> tags;
    private String dateOfPurchase;  // this can be a Date object
    private double estimatedValue;
    private String make;
    private String model;
    private String serialNumber;
    private String description;
    private String comment;

    /**
     * Constructor of an Item object
     *
     * @param  name  the name of the item
     * @param  tags stores a list of tags assigned to the item
     * @param  dateOfPurchase the date of purchase/acquisition of the  item
     * @param  estimatedValue the estimated value of the item
     * @param  make the make of the item
     * @param  model the model of the item
     * @param  serialNumber the serial number of the item (if applicable)
     * @param  description the description of the item
     * @param  comment the comment on the item
     */
    public Item(String name, ArrayList<Tag> tags, String dateOfPurchase, double estimatedValue, String make, String model, String serialNumber, String description, String comment) {
        this.name = name;
        this.tags = tags;
        this.dateOfPurchase = dateOfPurchase;
        this.estimatedValue = estimatedValue;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.description = description;
        this.comment = comment;
    }

    // testing constructor - this constructor allows faster testing for add/edit
    public Item(String name, double estimatedValue, String description) {
        this.name = name;
        this.estimatedValue = estimatedValue;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
