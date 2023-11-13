package com.example.blackbox;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;


import static java.util.regex.Pattern.matches;

import android.util.Log;
import android.widget.TextView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

public class InventorySortTest {
    // initialize main activity
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Adds item 1
     */
    public void addTestItem1() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Item 1"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(150)));
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
        onView(withId(R.id.small_save_button)).perform(click());
    }

    /**
     * This tests that sorting by value and ascending works
     */
    @Test
    public void testSortByValue(){
        InventoryDBTest.clearInventoryDB();
        addTestItem1();
        addTestItem2();
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by date
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
    }
}
