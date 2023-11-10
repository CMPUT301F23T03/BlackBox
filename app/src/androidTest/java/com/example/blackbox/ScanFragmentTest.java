package com.example.blackbox;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScanFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testScanFragment() {
        // Navigate to ScanFragment
        Espresso.onView(ViewMatchers.withId(R.id.scan)).perform(ViewActions.click());

        // Navigate to ScanCameraFragment
        Espresso.onView(ViewMatchers.withId(R.id.button_camera)).perform(ViewActions.click());

        // Test UI elements in ScanCameraFragment
        Espresso.onView(ViewMatchers.withId(R.id.top_constraint_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.title_edit_item)).check(ViewAssertions.matches(ViewMatchers.withText("Barcode Scanning")));
        Espresso.onView(ViewMatchers.withId(R.id.surface_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.barcode_text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Navigate back to ScanFragment
        Espresso.onView(ViewMatchers.withId(R.id.back_button)).perform(ViewActions.click());

        // Test UI elements in ScanFragment
        Espresso.onView(ViewMatchers.withId(R.id.top_constraint_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.title_edit_item)).check(ViewAssertions.matches(ViewMatchers.withText("Barcode Scanning")));
        Espresso.onView(ViewMatchers.withId(R.id.button_camera)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.button_gallery)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.textView)).check(ViewAssertions.matches(ViewMatchers.withText("Scan or insert a picture of Barcode")));

        // Navigate to ScanGalleryFragment
        Espresso.onView(ViewMatchers.withId(R.id.button_gallery)).perform(ViewActions.click());

        // Test UI elements in ScanGalleryFragment
        Espresso.onView(ViewMatchers.withId(R.id.top_constraint_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.title_edit_item)).check(ViewAssertions.matches(ViewMatchers.withText("Barcode Scanning")));
        Espresso.onView(ViewMatchers.withId(R.id.choose_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.image_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.barcode_text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Navigate back to ScanFragment
        Espresso.onView(ViewMatchers.withId(R.id.back_button)).perform(ViewActions.click());

    }
}

