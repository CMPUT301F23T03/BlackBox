package com.example.blackbox;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ItemImageTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES);

    @Before
    public void setUp(){
        // Disable animation before each test
        disableAnimations();
        addTestItem1();
    }

    @Test
    public void testAddImageAttachment() throws UiObjectNotFoundException {
        //click on sign in then wait???
        // click on the add button
        onView(withId(R.id.add_button)).perform(click());
        // scroll down
        onView(withId(R.id.item_list)).perform(ViewActions.scrollTo());
        // click on add button
        onView(withId(R.id.add_img_btn)).perform(click());
        // click camera
        onView(withId(R.id.camera_button)).perform(click());

        /*
        // click on capture button
        // Use UI Automator to find and click the capture button in the camera app
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject captureButton = device.findObject(new UiSelector().resourceId("com.android.camera:id/shutter_button"));
        captureButton.click();
         */

        // Test UI elements in Attach Image
        onView(withId(R.id.title_edit_item)).check(matches(withText("Attach Image")));
        onView(withId(R.id.image_view)).check(matches(ViewMatchers.isDisplayed()));

        // Wait for 2 seconds (2000 milliseconds)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // click on Gallery
        onView(withId(R.id.gallery_button)).perform(click());
        clickOnFirstImageInGallery();

        // check if images is displayed on the attach image fragment
        onView(withId(R.id.image_view)).check(matches(isDisplayed()));

        // click confirm
        onView(withId(R.id.confirm_button)).perform(click());

        // check if recycler view is displayed
        onView(withId(R.id.image_recycler_view)).check(matches(isDisplayed()));
    }

    private void clickOnFirstImageInGallery() {
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

    public void addTestItem1() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.name_editText)).perform(ViewActions.typeText("Item 1"));
        onView(withId(R.id.value_editText)).perform(ViewActions.replaceText(String.valueOf(150)));
        onView(withId(R.id.make_editText)).perform(ViewActions.replaceText("Toyota"));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disableAnimations(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Change system animation settings
        Settings.Global.putInt(
                context.getContentResolver(),
                Settings.Global.WINDOW_ANIMATION_SCALE, 0
        );
        Settings.Global.putInt(
                context.getContentResolver(),
                Settings.Global.TRANSITION_ANIMATION_SCALE, 0
        );
        Settings.Global.putInt(
                context.getContentResolver(),
                Settings.Global.ANIMATOR_DURATION_SCALE, 0
        );

        // Wait for the animation settings to take effect
        SystemClock.sleep(500);
    }
}
