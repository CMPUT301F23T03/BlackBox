package com.example.blackbox.inventory.filter;

import com.example.blackbox.inventory.Item;
import com.example.blackbox.tag.Tag;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;

/**
 Class used to encapsulate all of the information about a Filter
 */

public class Filter {

    private String[] filterTypes = {"date","price","make","tag","keyword"};
    private String filterType;
    private String filterName;

    //Used to store the items that were filtered out due to this type of filter
    private ArrayList<Item> itemList;


    /**
     * Returns the make input for a filter if it is of that type
     * @return
     *         String of the corresponding make
     */
    public String getMake() {
        return make;
    }


    /**
     * Takes String make as a parameter and sets it
     * @param make
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Gets the price range set for the given filter,
     * First value is the lower range,
     * Second value is the upper range
     * @return double[]
     */
    public double[] getPriceRange() {
        return priceRange;
    }

    /**
     * Sets the price range set for the given filter,
     * First value is the lower range,
     * Second value is the upper range
     * @param priceRange
     */
    public void setPriceRange(double[] priceRange) {
        this.priceRange = priceRange;
    }


    /**
     * Gets the date range set for the given filter,
     * First value is the lower range,
     * Second value is the upper range,
     * Format is 'yyyy-mm-dd'
     * @return String[]
     */
    public String[] getDateRange() {
        return dateRange;
    }

    /**
     * Sets the date range set for the given filter,
     * First value is the lower range,
     * Second value is the upper range,
     * Format is 'yyyy-mm-dd'
     * @param  dateRange
     */
    public void setDateRange(String[] dateRange) {
        this.dateRange = dateRange;
    }

    /**
     * Gets the ArrayList of tags selected for the filter
     * @return ArrayList of tags
     * @see Tag
     */
    public ArrayList<Tag> getTagArrayList() {
        return tagArrayList;
    }

    /**
     * Sets the ArrayList of tags selected for the filter
     * @param tagArrayList
     * @see Tag
     */
    public void setTagArrayList(ArrayList<Tag> tagArrayList) {
        this.tagArrayList = tagArrayList;
    }

    private String make = "";
    private double[] priceRange = new double[2];

    /**
     * Gets the array of keywords set for the filter
     * @return
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * Sets the array of keywords set for the filter
     * @param keywords
     */
    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    private String[] keywords;
    private String[] dateRange = new String[2];
    private ArrayList<Tag> tagArrayList = new ArrayList<>();

    /**
     * Helper function used to ensure that constructor input string
     *  matches of the allowed filterTypes.
     * @param inputFilter
     * @return true if input matches one of filterTypes, false otherwise
     */
    private boolean checkFilterType(String inputFilter){
        int numberOfMatches = 0;
        for (String filter: filterTypes){
            if (filter.compareToIgnoreCase(inputFilter) == 0){
                numberOfMatches +=1;
            }
        }
        if (numberOfMatches > 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Inputs given filterType for a filter, and initializes the object
     * @param filterType
     * @throws IllegalArgumentException If filterType is an illegal argument
     */
    public Filter(String filterType) throws IllegalArgumentException{
        this.itemList = new ArrayList<>();
        this.filterName = "";
        if (checkFilterType(filterType)){
            this.filterType=filterType;
        }else{
            throw new IllegalArgumentException("Filter type must be 'date', 'make', or 'price'");
        }
    }

    /**
     * Returns given filterType of the object
     * @return String
     */
    public String getFilterType(){
        return filterType;
    }

    /**
     * Adds given Item object to the list of Items that the filter is responsible
     *  for filtering out.
     * @param item
     * @see Item
     */
    public void addItemToFilter(Item item){
        itemList.add(item);
    }

    /**
     * Gets the list of items that the filter has filtered out as a result of the set filterType.
     * @return ArrayList of items
     * @see Item
     */
    public ArrayList<Item> getItemList(){
        return itemList;
    }

    /**
     * Set the name of filter to be displayed when shown by FilterListAdapter
     * @param name
     * @see FilterListAdapter
     */
    public void setFilterName(String name){
        this.filterName = name;
    }

    /**
     * Get the name of the filter to be displayed when shown by FilterListAdapter
     * @return
     * @see FilterListAdapter
     */
    public String getFilterName(){
        return this.filterName;
    }



}
