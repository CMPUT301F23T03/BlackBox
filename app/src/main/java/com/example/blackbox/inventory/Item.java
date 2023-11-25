package com.example.blackbox.inventory;

import android.annotation.SuppressLint;

import com.example.blackbox.tag.Tag;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * The `Item` class represents an inventory item, storing information such as name, tags, date of purchase,
 * estimated value, make, model, serial number, description, comment, ID, and date of the last update.
 * It also includes methods for retrieving and setting various properties of the item.
 * Instances of this class can be created using different constructors to accommodate various use cases.
 */
public class Item implements Serializable {

    // initialize data members
    // we might add a data member of reference to an image later on
    private String name;
    private String ID;
    private ArrayList<Tag> tags;
    private String dateOfPurchase;  // this can be a Date object
    private Double estimatedValue;
    private String make;
    private String model;
    private String serialNumber;
    private String description;
    private String comment;
    private Date dateUpdated;
    private boolean isSelected;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
    public Item(String name, ArrayList<Tag> tags, String dateOfPurchase, Double estimatedValue, String make, String model, String serialNumber, String description, String comment) {
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

    /**
     * Constructor of an Item object with an ID
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
     * @param  ID the id of the item in the database
     */
    public Item(String name, ArrayList<Tag> tags, String dateOfPurchase, Double estimatedValue, String make, String model, String serialNumber, String description, String comment, String ID) {
        this.name = name;
        this.tags = tags;
        this.dateOfPurchase = dateOfPurchase;
        this.estimatedValue = estimatedValue;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.description = description;
        this.comment = comment;
        this.ID = ID;
    }

    /**
     * Constructor for a simplified Item object primarily for testing purposes.
     *
     * @param name        The name of the item.
     * @param estimatedValue The estimated value of the item.
     * @param description The description of the item.
     */
    public Item(String name, Double estimatedValue, String description) {
        this.name = name;
        this.estimatedValue = estimatedValue;
        this.description = description;
    }
    /**
     * Constructor for a simplified Item object with ID primarily for testing purposes.
     *
     * @param name        The name of the item.
     * @param estimatedValue The estimated value of the item.
     * @param description The description of the item.
     * @param ID The id of the item.
     */
    public Item(String name, Double estimatedValue, String description, String ID) {
        this.name = name;
        this.estimatedValue = estimatedValue;
        this.description = description;
        this.ID = ID;
    }

    /**
     * Get the number of tags that an item has
     * @return
     *      The number of tags
     */
    public Integer getNumTags() {
        return tags.size();
    }

    // Getters and setters for various properties
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

    public Double getEstimatedValue() {
        return estimatedValue;
    }

    public String getStringEstimatedValue(){
        return String.format("%.2f", estimatedValue);
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public String getStringDateUpdated(){
        // Create a SimpleDateFormat object with the desired date format
        String dateStr = dateFormat.format(dateUpdated);
        return dateStr;
    }

    public void setDateUpdated(Date date){
        this.dateUpdated = date;
    }

    public void setDateUpdatedWithString(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateUpdated = dateFormat.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    /**
     * Returns the tag with the highest alphabetical president associated with the item
     * @return
     *         The tag with highest precedent
     */
    public Tag getHighestPrecedentTag(){
        Tag highestTag = null;
        if (tags != null) {
            for (Tag tag : tags) {
                if (highestTag == null || tag.getName().compareToIgnoreCase(highestTag.getName()) < 0) {
                    highestTag = tag;
                }
            }
        }
        return highestTag;
    }

}
