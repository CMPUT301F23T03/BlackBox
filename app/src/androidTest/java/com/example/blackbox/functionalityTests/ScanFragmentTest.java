package com.example.blackbox.functionalityTests;

import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.platform.app.InstrumentationRegistry;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.blackbox.MainActivity;
import com.example.blackbox.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * Espresso UI test for the ScanFragment functionality.
 */
@RunWith(AndroidJUnit4.class)
public class ScanFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES);

    @Test
    public void testScanFragment() throws UiObjectNotFoundException, InterruptedException {
        // Navigate to ScanFragment
        Espresso.onView(ViewMatchers.withId(R.id.scan)).perform(ViewActions.click());

        // Navigate to ScanCameraFragment
        Espresso.onView(ViewMatchers.withId(R.id.button_camera)).perform(ViewActions.click());

        // Test UI elements in ScanCameraFragment
        Espresso.onView(ViewMatchers.withId(R.id.top_constraint_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.title_edit_item)).check(ViewAssertions.matches(ViewMatchers.withText("Barcode Scanning")));
        Espresso.onView(ViewMatchers.withId(R.id.surface_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.barcode_text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Wait for 2 seconds (2000 milliseconds)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        // Click on "choose_button"
         Espresso.onView(ViewMatchers.withId(R.id.choose_button)).perform(ViewActions.click());

        clickOnFirstImageInGallery();

        // Navigate back to ScanFragment
        Espresso.onView(ViewMatchers.withId(R.id.back_button)).perform(ViewActions.click());

    }

    private void clickOnFirstImageInGallery() throws UiObjectNotFoundException, InterruptedException {
        // Use UI Automator to click on the first image in the Android gallery
        UiDevice uiDevice = UiDevice.getInstance((InstrumentationRegistry.getInstrumentation()));

        // Create a UiSelector to find the first image in the gallery by its resource ID
        UiSelector firstImageSelector = new UiSelector()
                .resourceId("com.android.gallery3d:id/grid")
                .childSelector(new UiSelector().index(0));

        UiObject firstImage = uiDevice.findObject(firstImageSelector);

        boolean isFirstImageFound = firstImage.exists();

        if (isFirstImageFound){
            try {
                firstImage.click();
            } catch (UiObjectNotFoundException e) {
                throw new RuntimeException(e);
            }
            uiDevice.pressBack();
        } else{
            Log.d("Image check", "Image not found");
            uiDevice.pressBack();
        }
    }
}

