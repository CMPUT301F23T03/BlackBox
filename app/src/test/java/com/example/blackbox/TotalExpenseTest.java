package com.example.blackbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class TotalExpenseTest {
    private ItemList generateExampleItemList(){
        ItemList itemList = new ItemList();
        // create tags
        Tag carTag = new Tag("Vehicle", Integer.valueOf(1), "Blue","A mode of transport");
        Tag electronicTag = new Tag("Electronic", Integer.valueOf(4), "Yellow","Any electronic device");
        Tag computerTag = new Tag("Computer", Integer.valueOf(4), "Yellow","Computers");
        // create tag lists
        ArrayList<Tag> carTags = new ArrayList<>();
        carTags.add(carTag);
        ArrayList<Tag> computerTags = new ArrayList<>();
        computerTags.add(electronicTag);

        // create items
        Item car = new Item("Car", carTags, "2023-05-03", Double.valueOf(30000), "Mazda", "Model 3", "12356134234", "2023", "I really like it");
        Item pc = new Item("PC", computerTags, "2018-05-03", Double.valueOf(1000), "Dell", "idk", "312359231", "My trusty computer", "Getting Slow");
        Item laptop = new Item("Laptop", computerTags, "2023-01-03", Double.valueOf(3000), "Apple", "MacBook Pro", "234234f32", "New laptop for school", "It's great");
        Item TV = new Item("Laptop", computerTags, "2016-05-23", Double.valueOf(150), "LG", "SmartTV", "d323123", "Old TV", "Starting to flicker");
        Item pencil = new Item("Pencil", null, "2023-11-03", Double.valueOf(1.99), "HB", "","2312313","a pencil", "I don't know why you would track this");

        // add items to list
        itemList.add(car);
        itemList.add(pc);
        itemList.add(laptop);
        itemList.add(TV);
        itemList.add(pencil);
        return  itemList;
    }
    @Test
    public void testWithRealisticData(){
        ItemList itemList = generateExampleItemList();
        assertEquals(Double.valueOf(34151.99), itemList.calculateTotalSum());
        assertEquals("$34,151.99",StringFormatter.getMonetaryString(itemList.calculateTotalSum()));
    }

    @Test
    public void testWithEmptyList(){
        ItemList itemList = new ItemList();
        assertEquals(Double.valueOf(0), itemList.calculateTotalSum());
        assertEquals("$0.00",StringFormatter.getMonetaryString(itemList.calculateTotalSum()));
    }
}
