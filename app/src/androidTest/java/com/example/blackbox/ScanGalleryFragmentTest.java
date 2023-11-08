package com.example.blackbox;

import static junit.framework.TestCase.assertEquals;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RunWith(AndroidJUnit4.class)
public class ScanGalleryFragmentTest {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testGetBarcode() {
        String[] serialNumbers = {"8544705444.jpg", "6038398546.jpg"}; // Image file names

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
                ScanGalleryFragment fragment = new ScanGalleryFragment();
                assertEquals(fragment.getBarcode(imageUri), serialNumber);
            } else {
                // Log a message or handle the case when the file does not exist
                Log.d("Test Barcode", "File does not exist: " + filePath);
            }
        }
    }

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
