package com.example.blackbox.inventory;

import com.example.blackbox.tag.Tag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents a list of items which extends default arraylist behavior
 * to include functionality such as comparison between items and
 * total expense calculation
 */
public class ItemList extends ArrayList<Item> {
    public ItemList (){
        super();
    }


    /**
     * Sort the list items by Date in ascending or descending order.
     * @param ascending specifies the sorting order, true = ascending, descending otherwise
     */
    public void sortByDate(Boolean ascending){
        Comparator<Item> dateComp = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                //parse the date into appropriate format
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
                try{
                    Date date1 = dateFormat.parse(o1.getDateOfPurchase());
                    Date date2 = dateFormat.parse(o2.getDateOfPurchase());
                    if (date1 != null && date2 != null) {
                        return ascending ? date1.compareTo(date2) : date2.compareTo(date1); // check for ascending or descending
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        this.sort(dateComp);
    }


    /**
     * Sort the list items by Estimated Value in ascending or descending order.
     * @param ascending specifies the sorting order, true = ascending, descending otherwise
     */
    public void sortByValue(Boolean ascending) {
        if (ascending){
            this.sort((item1, item2) -> Double.compare(item1.getEstimatedValue(), item2.getEstimatedValue()));
        }
        else {
            this.sort((item1, item2) -> Double.compare(item2.getEstimatedValue(), item1.getEstimatedValue()));
        }
    }

    /**
     * Sort the list items by Make (alphabetically) in ascending or descending order.
     * @param ascending specifies the sorting order, true = ascending, descending otherwise
     */
    public void sortByMake(Boolean ascending) {
        Comparator<Item> makeComp = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                String make1 = o1.getMake();
                String make2 = o2.getMake();

                // compare alphabetically
                int result = make1.compareToIgnoreCase(make2);
                // check for descending
                if (!ascending){
                    result = -result;
                }
                return result;
            }
        };
        this.sort(makeComp);
    }

    /**
     * Sort the list items by the Highest Precedent Tag (alphabetically) in ascending or descending order.
     * @param ascending specifies the sorting order, true = ascending, descending otherwise
     */
    public void sortByHighestPrecedentTag(Boolean ascending) {
        Comparator<Item> tagComp = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                int result;
                Tag tag1 = o1.getHighestPrecedentTag();
                Tag tag2 = o2.getHighestPrecedentTag();
                if (tag1 == null && tag2 == null){
                    result = 0;
                }
                else if (tag1 == null){
                    result = -1;
                }
                else if (tag2 == null){
                    result = 1;
                }
                else {
                    result = tag1.getName().compareToIgnoreCase(tag2.getName());
                }
                // check for descending
                if (!ascending) {
                    result = -result;
                }
                return result;
            }
        };
        this.sort(tagComp);
    }

    /**
     * Calculate the sum of the values of all items in the list
     * @return
     *      The calculated estimate as double
     */
    public Double calculateTotalSum() {
        Double totalSum = 0.0;
        for (Item item : this) {
            totalSum += item.getEstimatedValue();
        }
        return totalSum;
    }
}
