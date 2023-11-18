package com.example.blackbox;

import java.util.ArrayList;

public class Filter {

    private String[] filterNames = {"date","price","make",""};
    private String filterType;

    //Used to store the items that were filtered out due to this type of filter
    private ArrayList<Item> itemList;

    private boolean checkFilterType(String inputFilter){
        int numberOfMatches = 0;
        for (String filter: filterNames){
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
        if (checkFilterType(filterType)){
            this.filterType=filterType;
        }else{
            throw new IllegalArgumentException("Filter type must be 'date', 'make', or 'price'");
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
}
