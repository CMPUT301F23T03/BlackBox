package com.example.blackbox;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ItemImageTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES);

    @Test
    public void testAddImageAttachment() {
        InventoryDBTest.clearInventoryDB();
        addTestItem1();
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        // click on the add button
        onView(withId(R.id.add_button)).perform(click());
        // scroll down
        onView(withId(R.id.add_img_btn)).perform(ViewActions.scrollTo());
        // click on add button
        onView(withId(R.id.add_img_btn)).perform(click());
        // click camera
        onView(withId(R.id.camera_button)).perform(click());
        // click back
        device.pressBack();
        // Test UI elements in Attach Image
        onView(withId(R.id.title_edit_item)).check(matches(withText("Attach Image")));
        onView(withId(R.id.image_view)).check(matches(ViewMatchers.isDisplayed()));
        // Wait for 2 seconds (2000 milliseconds)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String imagePath = "/assets/imageAttach/063350702109.jpg";
        loadImageToGallery(InstrumentationRegistry.getInstrumentation().getContext(), imagePath);

        // click on Gallery
        onView(withId(R.id.gallery_button)).perform(click());
        clickOnFirstImageInGallery();

        // check if images is displayed on the attach image fragment
        onView(withId(R.id.image_view)).check(matches(isDisplayed()));

        // click confirm
//        onView(withId(R.id.confirm_button)).perform(click());
        // click back
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        // check if edit ui works
        // click on item
        //onView(withId(R.id.item_list)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(click());
        // scroll down
        onView(withId(R.id.add_img_btn)).perform(ViewActions.scrollTo());
        // click on add button
        onView(withId(R.id.add_img_btn)).perform(click());
        // click back
        onView(withId(R.id.back_button)).perform(click());
    }

    private void loadImageToGallery(Context context, String imagePath) {
        // Assuming imagePath is something like "imageAttach/063350702109.jpg" in your assets
        try {
            // Copy the image from assets to external storage
            File externalDir = context.getExternalFilesDir(null);
            File imageFile = new File(externalDir, "063350702109.jpg");

            // Copy the image from assets to external storage
            AssetManager assetManager = context.getAssets();
            try (InputStream in = assetManager.open(imagePath);
                 OutputStream out = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Notify the media scanner about the new file
            MediaScannerConnection.scanFile(context,
                    new String[]{imageFile.getAbsolutePath()},
                    null,
                    (path, uri) -> {
                        // Scanning completed, you can perform any additional actions here
                        Log.d("ImageScanner", "Scanned path: " + path);
                        Log.d("ImageScanner", "Scanned URI: " + uri);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        onView(withId(R.id.small_save_button)).perform(click());
        try {
            Thread.sleep(1000); // Add a 1-second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
