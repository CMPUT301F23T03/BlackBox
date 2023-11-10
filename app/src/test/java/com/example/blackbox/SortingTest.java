package com.example.blackbox;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * This tests that sorting is preformed correctly on an itemList
 */
public class SortingTest {
    private Tag carTag;
    private Tag electronicTag;
    private Tag computerTag;
    private Item car;
    private Item pc;
    private Item laptop;
    private Item TV;
    private Item pencil;
    private ItemList itemList;

    @Before
    public void generateExampleItemList(){
        itemList = new ItemList();
        // create tags
        carTag = new Tag("Vehicle", Integer.valueOf(1), "Blue","A mode of transport");
        electronicTag = new Tag("Electronic", Integer.valueOf(4), "Yellow","Any electronic device");
        computerTag = new Tag("Computer", Integer.valueOf(4), "Yellow","Computers");
        // create tag lists
        ArrayList<Tag> carTags = new ArrayList<>();
        carTags.add(carTag);
        ArrayList<Tag> electronicTags = new ArrayList<>();
        electronicTags.add(electronicTag);
        ArrayList<Tag> computerTags = new ArrayList<>();
        computerTags.add(electronicTag);
        computerTags.add(computerTag);

        // create items
        car = new Item("Car", carTags, "2023-05-03", Double.valueOf(30000), "Mazda", "Model 3", "12356134234", "2023", "I really like it");
        pc = new Item("PC", computerTags, "2018-05-03", Double.valueOf(1000), "Dell", "idk", "312359231", "My trusty computer", "Getting Slow");
        laptop = new Item("Laptop", computerTags, "2023-01-03", Double.valueOf(3000), "Apple", "MacBook Pro", "234234f32", "New laptop for school", "It's great");
        TV = new Item("Laptop", electronicTags, "2016-05-23", Double.valueOf(150), "LG", "SmartTV", "d323123", "Old TV", "Starting to flicker");
        pencil = new Item("Pencil", null, "2023-11-03", Double.valueOf(1.99), "HB", "","2312313","a pencil", "I don't know why you would track this");

        // add items to list
        itemList.add(car);
        itemList.add(pc);
        itemList.add(laptop);
        itemList.add(TV);
        itemList.add(pencil);
    }
    @Test
    public void testSortByValue(){
        itemList.sortByValue(Boolean.TRUE);
        assertEquals(pencil, itemList.get(0));
        assertEquals(TV, itemList.get(1));
        assertEquals(pc, itemList.get(2));
        assertEquals(laptop, itemList.get(3));
        assertEquals(car, itemList.get(4));

        itemList.sortByValue(Boolean.FALSE);
        assertEquals(pencil, itemList.get(4));
        assertEquals(TV, itemList.get(3));
        assertEquals(pc, itemList.get(2));
        assertEquals(laptop, itemList.get(1));
        assertEquals(car, itemList.get(0));
    }

    @Test
    public void testSortByTag(){
        itemList.sortByHighestPrecedentTag(Boolean.TRUE);
        assertEquals(pencil, itemList.get(0));
        assertEquals(pc, itemList.get(1));
        assertEquals(laptop, itemList.get(2));
        assertEquals(TV, itemList.get(3));
        assertEquals(car, itemList.get(4));

        itemList.sortByHighestPrecedentTag(Boolean.FALSE);
        assertEquals(pencil, itemList.get(4));
        assertEquals(laptop, itemList.get(3));
        assertEquals(pc, itemList.get(2));
        assertEquals(TV, itemList.get(1));
        assertEquals(car, itemList.get(0));
        // Note: some items may be allowed in more than one position
    }

    @Test
    public void testSortByMake(){
        itemList.sortByMake(Boolean.TRUE);
        assertEquals(laptop, itemList.get(0));
        assertEquals(pc, itemList.get(1));
        assertEquals(pencil, itemList.get(2));
        assertEquals(TV, itemList.get(3));
        assertEquals(car, itemList.get(4));

        itemList.sortByMake(Boolean.FALSE);
        assertEquals(laptop, itemList.get(4));
        assertEquals(pc, itemList.get(3));
        assertEquals(pencil, itemList.get(2));
        assertEquals(TV, itemList.get(1));
        assertEquals(car, itemList.get(0));

    }

    @Test
    public void testSortByDate(){
        itemList.sortByDate(Boolean.TRUE);
        assertEquals(TV, itemList.get(0));
        assertEquals(pc, itemList.get(1));
        assertEquals(laptop, itemList.get(2));
        assertEquals(car, itemList.get(3));
        assertEquals(pencil, itemList.get(4));

        itemList.sortByDate(Boolean.FALSE);
        assertEquals(TV, itemList.get(4));
        assertEquals(pc, itemList.get(3));
        assertEquals(laptop, itemList.get(2));
        assertEquals(car, itemList.get(1));
        assertEquals(pencil, itemList.get(0));
    }

}
