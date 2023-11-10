package com.example.blackbox;

import static androidx.test.espresso.Espresso.onData;
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
    // initialize main activity
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    public void setup(){
        // start with a fresh database
        InventoryDBTest.clearInventoryDB();
        // click on addItem
        onView(withId(R.id.add_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Random Test"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(150)));
        // Press add
        onView(withId(R.id.small_save_button)).perform(click());
        // click on addItem
        onView(withId(R.id.add_button)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("test 2"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(100)));
        // Press add
        onView(withId(R.id.small_save_button)).perform(click());
    }

    @Test
    public void testSortByValue(){
        setup();
        // Click on the "Sort" button
        onView(withId(R.id.sort_button)).perform(click());
        // Choose sort by date
        onView(withId(R.id.sort_category_spinner)).perform(click());
        onView(withText("By Value")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        //Choose sort order as ascending
        onView(withId(R.id.sort_order_spinner)).perform(click());
        onView(withText("Ascending")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        //Click OK
        onView(withText("OK")).perform(click());
        onView(allOf(isAssignableFrom(TextView.class), withId(R.id.name))).check(matches(withText("test 2")));
    }
}
