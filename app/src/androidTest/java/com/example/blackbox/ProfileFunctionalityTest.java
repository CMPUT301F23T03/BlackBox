package com.example.blackbox;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The ProfileFunctionalityTest class contains unit tests for the ProfileEditFragment class methods.
 * User MUST already be logged in for all these tests to pass
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFunctionalityTest {

    /**
     * Initialize MainActivity
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * This test checks if the activity is accessible by clicking the correct navigation button
     */
    @Test
    public void testSwitchActivity(){
        onView(withId(R.id.profile_fragment)).check(doesNotExist());
        onView(withId(R.id.profile)).perform(click());
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()));
    }

    /**
     * This test checks if an profile can be edited (name and bio)
     */
    @Test
    public void testEditProfileActivity(){
        // navigate to view
        onView(withId(R.id.profile)).perform(click());
        // click on edit
        onView(withId(R.id.edit_profile)).perform(click());
        // fill information
        onView(withId(R.id.name_editText)).perform(ViewActions.clearText());
        onView(withId(R.id.bio_editText)).perform(ViewActions.clearText());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Name1"));
        //onView(withId(R.id.bio_editText)).perform(ViewActions.typeText("Era of Computer Science"));
        // edit item
        onView(withId(R.id.small_save_button)).perform(click());
        // see if the newly edited data is displayed
        onView(withText("Name1")).check(matches(isDisplayed()));
        //onView(withText("Era of Computer Science")).check(matches(isDisplayed()));
    }

    /**
     * Test that the back button navigates back to the profile fragment
     */
    @Test
    public void testBackButton() {
        // navigate to view
        onView(withId(R.id.profile)).perform(click());
        // click on edit
        onView(withId(R.id.edit_profile)).perform(click());
        // click on backButton
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()));
    }
}
