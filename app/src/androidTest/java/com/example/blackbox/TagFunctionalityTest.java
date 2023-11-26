package com.example.blackbox;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.blackbox.tag.Tag;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TagFunctionalityTest {
    final private String tagName = "Test Tag";
    final private String description = "This is an example description to be used in testing";
    final private String tagName2 = "A different name";
    final private String itemName = "Item Name";
    final private String description2 = "This is a different example description to be used in testing";

    final private Integer maxDelay = 1000;  // the number of milliseconds the app can wait for

    // initialize main activity
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);


    /**
     * Clear everything in the database
     */
    private void clearDBs(){
        // clear inventory first to avoid errors
        InventoryDBTest.clearInventoryDB();
        TagDBTest.clearTagDB();
    }

    /**
     * This test checks if the activity is accessible by clicking the bottom right icon
     */
    @Test
    public void testSwitchActivity(){
        onView(withId(R.id.tag_fragment)).check(doesNotExist());
        onView(withId(R.id.settings)).perform(click());
        onView(withId(R.id.tag_fragment)).check(matches(isDisplayed()));
    }



    /**
     * This test checks if a tag can be added
     */
    @Test
    public void testAdd(){
        // start with a fresh database
        clearDBs();
        // navigate to view
        onView(withId(R.id.settings)).perform(click());
        // click on addItem
        onView(withId(R.id.add_tag_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(tagName));
        onView(withId(R.id.desc_editText)).perform(ViewActions.typeText(description));
        onView(withId(R.id.color_spinner)).perform(click());
        onView(withText("Orange")).perform(click());
        // add item
        onView(withId(R.id.small_save_button)).perform(click());
        // see if the newly added data is displayed
        onView(withText(tagName)).check(matches(isDisplayed()));
        onView(withText(description)).check(matches(isDisplayed()));
        onView(withText(tagName)).perform(click());
        onView(withText("Orange")).check(matches(isDisplayed()));

    }

    /**
     * This test checks if a tag can be edited
     */
    @Test
    public void testEdit(){
        // start with a fresh database
        clearDBs();
        // navigate to view
        onView(withId(R.id.settings)).perform(click());
        // click on addItem
        onView(withId(R.id.add_tag_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(tagName));
        onView(withId(R.id.desc_editText)).perform(ViewActions.typeText(description));
        // add item
        onView(withId(R.id.small_save_button)).perform(click());
        // open the newly added item
        onData(is(instanceOf(Tag.class))).inAdapterView(withId(R.id.tag_view)).atPosition(0).perform(click());
        // change the name and description
        onView(withId(R.id.name_editText)).perform(ViewActions.clearText());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(tagName2));
        onView(withId(R.id.desc_editText)).perform(ViewActions.clearText());
        onView(withId(R.id.desc_editText)).perform(ViewActions.typeText(description2));
        // update item
        onView(withId(R.id.save_tag_button)).perform(click());
        // check if the displayed values are correct
        onView(withText(tagName2)).check(matches(isDisplayed()));
        onView(withText(description2)).check(matches(isDisplayed()));
    }

    /**
     * This test checks if a tag can be deleted
     */
    @Test
    public void testDelete() {
        // start with a fresh database
        clearDBs();
        // navigate to view
        onView(withId(R.id.settings)).perform(click());
        // click on addItem
        onView(withId(R.id.add_tag_button)).perform(click());
        // fill information

        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(tagName));
        onView(withId(R.id.desc_editText)).perform(ViewActions.typeText(description));
        // add item
        onView(withId(R.id.small_save_button)).perform(click());

        // open the newly added item
        onData(is(instanceOf(Tag.class))).inAdapterView(withId(R.id.tag_view)).atPosition(0).perform(click());
        // delete the item
        onView(withId((R.id.delete_tag_button))).perform(click());
        onView(withText("CONFIRM")).perform(click());

        // wait for deletion to occur
        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }

        onView(withText(tagName)).check(doesNotExist());

    }

    /**
     * Test that the back button navigates back to the tag fragment
     */
    @Test
    public void testBackButton() {
        // navigate to view
        onView(withId(R.id.settings)).perform(click());
        // click on addItem
        onView(withId(R.id.add_tag_button)).perform(click());
        // click on backButton
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.tag_fragment)).check(matches(isDisplayed()));
    }

    /**
     * Test that tags can be added to an item and that when they
     * are deleted they are automatically removed from the item
     */
    @Test
    public void testAddDeleteWithItem(){
        // start with a fresh database
        clearDBs();
        // navigate to tag page
        onView(withId(R.id.settings)).perform(click());
        // click on addItem
        onView(withId(R.id.add_tag_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(tagName));
        onView(withId(R.id.desc_editText)).perform(ViewActions.typeText(description));
        onView(withId(R.id.small_save_button)).perform(click());

        // navigate to inventory page
        onView(withId(R.id.inventory)).perform(click());
        // add an item with new tag
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.tag_dropdown)).perform(click());
        // wait for popup to fetch tags
        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }
        onView(withText(tagName)).perform(click());
        onView(withText(tagName)).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(itemName));
        onView(withId(R.id.value_editText)).perform(ViewActions.typeText("1"));
        onView(withId(R.id.small_save_button)).perform(click());
        // wait for item to be saved
        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }


        // navigate back to tag page and delete tag
        onView(withId(R.id.settings)).perform(click());
        onView(withText(tagName)).perform(click());
        onView(withId(R.id.delete_tag_button)).perform(click());
        onView(withText("CONFIRM")).perform(click());
        // wait for tag to be deleted
        try {
            Thread.sleep(maxDelay);
        }
        catch (Exception e){
            Log.d("Sleep", "Exception");
        }
        // check that the tag no longer shows up
        onView(withText(tagName)).check(doesNotExist());


        //navigate back to item and check that tag is no longer attached
        onView(withId(R.id.inventory)).perform(click());
        onView(withText(itemName)).perform(click());
        onView(withText(tagName)).check(doesNotExist());

    }
}
