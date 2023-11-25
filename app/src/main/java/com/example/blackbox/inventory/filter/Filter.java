package com.example.blackbox.inventory.filter;

import com.example.blackbox.inventory.Item;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;

public class Filter {

    private String[] filterTypes = {"date","price","make","tag"};
    private String filterType;
    private String filterName;

    //Used to store the items that were filtered out due to this type of filter
    private ArrayList<Item> itemList;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public double[] getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(double[] priceRange) {
        this.priceRange = priceRange;
    }

    public String[] getDateRange() {
        return dateRange;
    }

    public void setDateRange(String[] dateRange) {
        this.dateRange = dateRange;
    }

    public ArrayList<Tag> getTagArrayList() {
        return tagArrayList;
    }

    public void setTagArrayList(ArrayList<Tag> tagArrayList) {
        this.tagArrayList = tagArrayList;
    }

    private String make = "";
    private double[] priceRange = new double[2];
    private String[] dateRange = new String[2];
    private ArrayList<Tag> tagArrayList = new ArrayList<>();


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

    public Filter(String filterType) throws IllegalArgumentException{
        this.itemList = new ArrayList<>();
        this.filterName = "";
        if (checkFilterType(filterType)){
            this.filterType=filterType;
        }else{
            throw new IllegalArgumentException("Filter type must be 'date', 'make', or 'price'");
        }
    }

    public Filter(String filterType, String filterName) throws IllegalArgumentException{
        this.itemList = new ArrayList<>();
        this.filterName = filterName;
        if (checkFilterType(filterType)){
            this.filterType=filterType;
        }else{
            throw new IllegalArgumentException("Filter type must be 'date', 'make', 'tag', or 'price'");
        }
    }

    public String getFilterType(){
        return filterType;
    }

    public void addItemToFilter(Item item){
        itemList.add(item);
    }

    public ArrayList<Item> getItemList(){
        return itemList;
    }

    public void setFilterName(String name){
        this.filterName = name;
    }

    public String getFilterName(){
        return this.filterName;
    }



}
