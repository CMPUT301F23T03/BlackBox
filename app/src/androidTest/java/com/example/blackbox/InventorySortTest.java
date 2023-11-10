package com.example.blackbox;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;


import static java.util.regex.Pattern.matches;

import android.widget.TextView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class InventorySortTest {

    /**
     * Method used to clear all DBs for testing
     */
    private void clearDBs(){
        // clear inventory first to avoid errors
        InventoryDBTest.clearInventoryDB();
        TagDBTest.clearTagDB();
    }

    // initialize main activity
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    public void setup(){
        clearDBs();

        //Mock data for testing
        //click on add button
        onView(withId(R.id.add_button)).perform(click());
        //input name
        onView(withId(R.id.name_editText)).perform(ViewActions.replaceText("Car"));
        //input value
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText("100"));


    }


    @Test
    public void testSortByDate(){
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by date
        onView(withId(R.id.sort_category_spinner)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)), is("By Date")))
                .perform(click());
        //Choose sort order as ascending
        onView(withId(R.id.sort_order_spinner)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)), is("Ascending")))
                .perform(click());
        //Click OK
        onView(withText("OK"))
                .perform(click());

        onView(allOf(isAssignableFrom(TextView.class), withId(R.id.name)))
                .check(matches(withText("item1")));



    }
}
