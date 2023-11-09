package com.example.blackbox;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.checkerframework.checker.units.qual.A;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class InventoryFragmentTest {
    final private String name = "Test Item";
    final private ArrayList<Tag> tags = new ArrayList<>();
    final private double estimatedValue = 120;
    final private String dateOfPurchase = "2023-11-08";
    final private String make = "12";
    final private String model = "12";
    final private String serialNumber = "789";
    final private String description = "Hi";
    final private String comment = "This test item is beautiful";
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * This test checks if the InventoryFragment is accessible by clicking the bottom left icon
     */
    @Test
    public void testInventorySwitchActivity(){
        onView(withId(R.id.inventory)).perform(click());
        onView(withId(R.id.inventory_fragment)).check(matches(isDisplayed()));
    }

    /**
     * This test checks if an item can be added
     * For now, only checks for name and price against the list in the InventoryFragment
     * It's better to check an item against a ViewFragment of itself - will implement later
     */
    @Test
    public void testAddItemActivity(){
        // start with a fresh database
        InventoryDBTest.clearInventoryDB();
        // click on add item
        onView(withId(R.id.add_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(name));
        onView(withId(R.id.value_editText)).perform(ViewActions.typeText(String.valueOf(estimatedValue)));

        // This isn't working yet - I need to find a way to scroll down
        // onView(withId(R.id.comment_editText)).perform(ViewActions.scrollTo());
        // onView(withId(R.id.desc_editText)).perform(ViewActions.typeText(description));

        // add item
        onView(withId(R.id.small_save_button)).perform(click());
        // see if the newly added data is displayed
        onView(withText(name)).check(matches(isDisplayed()));
        onView(withText(StringFormatter.getMonetaryString(estimatedValue))).check(matches(isDisplayed()));

        // This isn't working yet
        //onView(withText(description)).check(matches(isDisplayed()));
    }

    /**
     * This test checks if an item can be edited
     * For now, only checks for name and price against the list in the InventoryFragment
     * It's better to check an item against a ViewFragment of itself - will implement later
     */
    @Test
    public void testEditItemActivity(){
        // start with a fresh database
        InventoryDBTest.clearInventoryDB();
        // click on addItem
        onView(withId(R.id.add_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Random Test"));
        onView(withId(R.id.value_editText)).perform(ViewActions.typeText(String.valueOf(150)));
        // add item
        onView(withId(R.id.small_save_button)).perform(click());
        // open the newly added item
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        // change the name and value
        onView(withId(R.id.name_editText)).perform(ViewActions.clearText());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(name));
        onView(withId(R.id.value_editText)).perform(ViewActions.clearText());
        onView(withId(R.id.value_editText)).perform(ViewActions.typeText(String.valueOf(estimatedValue)));
        // update item
        onView(withId(R.id.small_save_button)).perform(click());
        // check if the displayed values are correct
        onView(withText(name)).check(matches(isDisplayed()));
        onView(withText(StringFormatter.getMonetaryString(estimatedValue))).check(matches(isDisplayed()));
    }

    /**
     * This test checks if an item can be deleted
     */
    @Test
    public void testDeleteItemActivity() {
        // start with a fresh database
        InventoryDBTest.clearInventoryDB();
        // click on addItem
        onView(withId(R.id.add_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText(name));
        onView(withId(R.id.value_editText)).perform(ViewActions.typeText(String.valueOf(estimatedValue)));
        // add item
        onView(withId(R.id.small_save_button)).perform(click());
        // open the newly added item
        onData(is(instanceOf(Item.class))).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        // delete the item
        onView(withId((R.id.delete_item_button))).perform(click());
        onView(withText("CONFIRM")).perform(click());
        // check that item no longer exists
        onView(withText(name)).check(doesNotExist());
    }

    @Test
    public void testBackButton() {
        // click on addItem
        onView(withId(R.id.add_button)).perform(click());
        // click on backButton
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.inventory_fragment)).check(matches(isDisplayed()));
    }
}
