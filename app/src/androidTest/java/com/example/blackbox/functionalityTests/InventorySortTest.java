package com.example.blackbox.functionalityTests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;


import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.blackbox.DBTests.InventoryDBTest;
import com.example.blackbox.MainActivity;
import com.example.blackbox.R;
import com.example.blackbox.DBTests.TagDBTest;
import com.example.blackbox.inventory.Item;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class InventorySortTest {
    final private Integer maxDelay = 1000;
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * A method which sets up items and tags to be sorted
     */
    @Before
    public void setup(){
        InventoryDBTest.clearInventoryDB();
        TagDBTest.clearTagDB();
        addTestTags();
        addTestItem1();
        addTestItem2();
        addTestItem3();
    }

    /**
     * Method used to clear all the tags from the database and then make new tags for the test
     */
    public void addTestTags() {
        // Switch to the tag fragment
        TagFunctionalityTest.navigateToTags();
        // Click on the "Add" button to add a new tag.
        onView(ViewMatchers.withId(R.id.add_tag_button)).perform(click());

        // Create and add the first tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag1"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag1 Description"));

        // Select a color for the first tag (e.g., Orange)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Orange")).perform(click());

        // Click the "Add" button to add the first tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the second tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag2"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag2 Description"));

        // Select a color for the second tag (e.g., Blue)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Blue")).perform(click());

        // Click the "Add" button to add the second tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the third tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Tag3"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Tag3 Description"));

        // Select a color for the third tag (e.g., Red)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Red")).perform(click());

        // Click the "Add" button to add the third tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Click on the "Add" button to add a new tag.
        onView(withId(R.id.add_tag_button)).perform(click());

        // Create and add the forth tag
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Antique"));
        onView(withId(R.id.desc_editText)).perform(ViewActions.replaceText("Items from olden times"));

        // Select a color for the forth tag (e.g., Purple)
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Purple")).perform(click());

        // Click the "Add" button to add the forth tag
        onView(withId(R.id.small_save_button)).perform(click());

        // Switch back to the item fragment screen
        TagFunctionalityTest.navigateFromTagsToItems();
    }

    /**
     * Adds item 1
     */
    public void addTestItem1() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Item 1"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(150)));
        onView(withId(R.id.make_editText)).perform(ViewActions.replaceText("Toyota"));
        onView(withId(R.id.tag_dropdown)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Tag1")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds item 2
     */
    public void addTestItem2() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Item 2"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(100)));
        onView(withId(R.id.make_editText)).perform(ViewActions.replaceText("Audi"));
        onView(withId(R.id.tag_dropdown)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Tag2")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds item 3
     */
    public void addTestItem3() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Item 3"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(200)));
        onView(withId(R.id.make_editText)).perform(ViewActions.replaceText("Mercedes"));
        onView(withId(R.id.tag_dropdown)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Antique")).perform(click());
        onView(withText("Tag3")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This tests that sorting by value for both ascending and descending order
     */
    @Test
    public void testSortByValue(){
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by value
        onView(withId(R.id.sort_category_spinner)).perform(click());
        onView(withText("By Value")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose sort order as ascending
        onView(withId(R.id.sort_order_spinner)).perform(click());
        onView(withText("Ascending")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Click OK
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // check the sorted order
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        onView(withText("Item 2")).check(matches(isDisplayed()));
        // Click on back button
        onView(withId(R.id.back_button)).perform(click());
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by value
        onView(withId(R.id.sort_category_spinner)).perform(click());
        onView(withText("By Value")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose sort order as descending
        onView(withId(R.id.sort_order_spinner)).perform(click());
        onView(withText("Descending")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Click OK
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // check the sorted order
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        onView(withText("Item 3")).check(matches(isDisplayed()));
    }

    /**
     * This tests that sorting my make for both ascending and descending order
     */
    @Test
    public void testSortByMake(){
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by make
        onView(withId(R.id.sort_category_spinner)).perform(click());
        onView(withText("By Make")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose sort order as ascending
        onView(withId(R.id.sort_order_spinner)).perform(click());
        onView(withText("Ascending")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Click OK
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // check the sorted order
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        onView(withText("Item 2")).check(matches(isDisplayed()));
        // Click on back button
        onView(withId(R.id.back_button)).perform(click());
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by make
        onView(withId(R.id.sort_category_spinner)).perform(click());
        onView(withText("By Make")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose sort order as descending
        onView(withId(R.id.sort_order_spinner)).perform(click());
        onView(withText("Descending")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Click OK
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // check the sorted order
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        onView(withText("Item 1")).check(matches(isDisplayed()));
    }

    @Test
    public void testSortByTag(){
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by tag
        onView(withId(R.id.sort_category_spinner)).perform(click());
        onView(withText("By Tag")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose sort order as ascending
        onView(withId(R.id.sort_order_spinner)).perform(click());
        onView(withText("Ascending")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Click OK
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // check the sorted order
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        onView(withText("Item 3")).check(matches(isDisplayed()));
        // Click on back button
        onView(withId(R.id.back_button)).perform(click());
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by tag
        onView(withId(R.id.sort_category_spinner)).perform(click());
        onView(withText("By Tag")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Choose sort order as descending
        onView(withId(R.id.sort_order_spinner)).perform(click());
        onView(withText("Descending")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        // Click OK
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // check the sorted order
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        onView(withText("Item 2")).check(matches(isDisplayed()));

    }
}

