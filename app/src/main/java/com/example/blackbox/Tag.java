package com.example.blackbox;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * This class represents a tag which can be applied to an item for the purposes of
 * sorting and filtering
 * Tags contain data such as name, databaseID, color, and description,
 * and provide methods for updating or retrieving data.
 */
public class Tag implements Serializable {
    private String name;
    private String description;
    private int color;
    private String colorName;
    private String dataBaseID;
    private Date dateUpdated;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String userID;

    /**
     * Constructor of an Tag object
     *
     * @param  name  the name of the tag
     */

    public Tag(String name, int color, String colorName, String description) {
        this.name = name;
        this.color = color;
        this.colorName = colorName;
        this.description = description;
    }
    public Tag(String name, int color, String colorName, String description, String dataBaseID) {
        this(name,color,colorName, description);
        this.dataBaseID = dataBaseID;
        this.dateUpdated = dateUpdated;
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

    public void setID(String ID){
        this.dataBaseID = ID;
    }
}
