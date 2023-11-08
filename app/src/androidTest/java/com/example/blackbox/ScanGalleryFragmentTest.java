import android.content.Context;
import android.net.Uri;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.blackbox.ScanGalleryFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ScanGalleryFragmentTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testSerialNumberExtraction() {
        // Assuming you have images in the res/drawable folder with serial numbers as names.
        // For example, "12345.jpg", "67890.jpg", etc.
        String[] serialNumbers = {"12345", "67890"};  // Add the names of your images here

        for (String serialNumber : serialNumbers) {
            // Construct the image URI for each serial number
            int imageResId = context.getResources().getIdentifier(serialNumber, "drawable", context.getPackageName());
            Uri imageUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + imageResId);

            // Create the fragment and test barcode extraction
            ScanGalleryFragment fragment = new ScanGalleryFragment();
            fragment.getBarcode(imageUri); // Call the method you want to test
            // Add assertions to check if the barcode extraction was successful
        }
    }
}
