package com.example.blackbox;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.vision.barcode.Barcode;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/**
 * This is a test class for the `ScanGalleryFragment` in an Android application. It uses the Espresso
 * testing framework and AndroidJUnit4 for UI testing and barcode extraction testing. The purpose of
 * this test is to ensure that the `ScanGalleryFragment` correctly extracts barcodes from images.
 *
 * The test involves:
 * - Copying an image file from the assets to the device's internal storage.
 * - Checking if the image file exists.
 * - Extracting barcodes from the image.
 * - Comparing the extracted barcode with the expected barcode.
 * - Cleaning up by deleting the copied image files after testing is done.
 *
 * This class is annotated with `@RunWith(AndroidJUnit4.class` to indicate that it's an Android JUnit
 * test. It uses `ActivityScenarioRule` to launch the `MainActivity` activity before running the test.
 */
@RunWith(AndroidJUnit4.class)
public class ScanGalleryFragmentTest {
    private Context context;
    private ScanGalleryFragment fragment;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Set up the test by initializing the `ScanGalleryFragment` and obtaining the context.
     */
    @Before
    public void setUp() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            fragment = new ScanGalleryFragment(activity);
            fragment.setContext(activity);
        });
        context = fragment.getContext();

    }

    /**
     * Test barcode extraction from images in the `ScanGalleryFragment`.
     */
    @Test
    public void testGetBarcode() {
        onView(withId(R.id.contentFragment))
                .check(matches(isDisplayed()));
        String[] serialNumbers = {"063350702109.jpg"};

        for (String serialNumber : serialNumbers) {
            // Copy the image file from assets to the device's internal storage
            String filePath = context.getFilesDir() + File.separator + serialNumber;
            copyImageFromAssets(serialNumber, filePath);

            File imageFile = new File(filePath);

            if (imageFile.exists()) {
                // Log the filePath for debugging
                Log.d("Test Barcode", "Image filePath: " + filePath);

                // Create the fragment and test barcode extraction
                Uri imageUri = Uri.fromFile(imageFile);
                SparseArray<Barcode> expectedBarcode =  fragment.getBarcode(imageUri);
                String stringBarcode = expectedBarcode.valueAt(0).displayValue;

                assertEquals(serialNumber, stringBarcode +".jpg");
            } else {
                // Log a message or handle the case when the file does not exist
                Log.d("Test Barcode", "File does not exist: " + filePath);
            }
        }
    }

    /**
     * Clean up by deleting the copied image files after testing is done.
     */
    @After
    public void cleanup() {
        // Delete the copied image files when the test is done
        String[] serialNumbers = {"063350702109.jpg"};

        for (String serialNumber : serialNumbers) {
            String filePath = context.getFilesDir() + File.separator + serialNumber;
            File imageFile = new File(filePath);

            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    Log.d("Test Cleanup", "Deleted file: " + filePath);
                } else {
                    Log.d("Test Cleanup", "Failed to delete file: " + filePath);
                }
            }
        }
    }

    /**
     * Copies an image from the assets folder to a specified destination file path.
     *
     * @param fileName     The name of the image file in the assets folder.
     * @param destFilePath The destination file path to which the image will be copied.
     */
    private void copyImageFromAssets(String fileName, String destFilePath) {
        try {
            InputStream in = context.getAssets().open(fileName);
            OutputStream out = new FileOutputStream(destFilePath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
