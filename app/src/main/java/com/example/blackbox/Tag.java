package com.example.blackbox;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


/**
 * The `Tag` class represents a tag associated with items in the inventory.
 * Tags are used to categorize and organize items based on specific criteria.
 * Each tag has a name, color, description, and optional database ID and last updated date.
 */
public class Tag implements Serializable {

    // Fields
    private String name;             // The name of the tag
    private String description;      // The description of the tag
    private int color;               // The color code of the tag
    private String colorName;        // The name of the color associated with the tag
    private String dataBaseID;       // The optional database ID associated with the tag
    private Date dateUpdated;        // The date when the tag was last updated

    // Date format for dateUpdated field
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs a new Tag object with the specified name, color, color name, and description.
     *
     * @param name         The name of the tag.
     * @param color        The color code of the tag.
     * @param colorName    The name of the color associated with the tag.
     * @param description  The description of the tag.
     */
    public Tag(String name, int color, String colorName, String description) {
        this.name = name;
        this.color = color;
        this.colorName = colorName;
        this.description = description;
    }

    /**
     * Constructs a new Tag object with the specified name, color, color name, description, and database ID.
     *
     * @param name         The name of the tag.
     * @param color        The color code of the tag.
     * @param colorName    The name of the color associated with the tag.
     * @param description  The description of the tag.
     * @param dataBaseID   The database ID associated with the tag.
     */
    public Tag(String name, int color, String colorName, String description, String dataBaseID) {
        this(name, color, colorName, description);
        this.dataBaseID = dataBaseID;
        // The dateUpdated field is not set explicitly in this constructor.
    }

    // Getters

    public String getName() {
        return name;
    }

    public String getHexStringColor() {
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

    public String getStringDateUpdated() {
        // Create a SimpleDateFormat object with the desired date format
        String dateStr = dateFormat.format(dateUpdated);
        return dateStr;
    }

    // Setters

    public void setDateUpdated(Date date) {
        this.dateUpdated = date;
    }

    public void setDateUpdatedWithString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateUpdated = dateFormat.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setID(String ID) {
        this.dataBaseID = ID;
    }
}
