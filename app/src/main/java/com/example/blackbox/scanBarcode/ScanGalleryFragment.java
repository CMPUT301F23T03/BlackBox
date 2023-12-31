package com.example.blackbox.scanBarcode;


import static android.Manifest.permission.READ_MEDIA_IMAGES;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.blackbox.MainActivity;
import com.example.blackbox.R;
import com.example.blackbox.scanBarcode.handler.BarcodeHandleChain;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Fragments of this class are responsible for allowing the user to select a barcode
 * image and automatically filling information from that image into an InventoryAddFragment
 * if the image is valid
 */

public class ScanGalleryFragment extends Fragment {
    private BarcodeDetector barcodeDetector;
    private TextView barcodeText;
    private String barcodeData;
    private ImageView imageView;
    private Button choosePictureButton;
    private Context context;
    private Activity testActivity;
    private ToneGenerator toneGen1;
    private BarcodeHandleChain barcodeHandleChain;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    imageView.setImageURI(uri);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    SparseArray<Barcode> barcodes = getBarcode(uri);
                    checkBarcode(barcodes);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });
    // Define a constant for the request code
    private static final int REQUEST_GALLERY_PERMISSION = 200;

    /**
     * Constructor used for testing
     *
     * @param activity
     */
    public ScanGalleryFragment(MainActivity activity) {
        this.testActivity = activity;
    }

    public ScanGalleryFragment() {
    }


    /**
     * A method which creates the view and sets up UI elements and listeners
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *      The created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // disable navigation bar
        ((MainActivity) requireActivity()).toggleBottomNavigationView(false);

        View view = inflater.inflate(R.layout.gallery_scan_fragment, container, false);
        imageView = view.findViewById(R.id.image_view);
        barcodeText = view.findViewById(R.id.barcode_text);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        barcodeHandleChain = new BarcodeHandleChain();

        choosePictureButton = view.findViewById(R.id.choose_button);
        choosePictureButton.setOnClickListener(v -> {
            openGallery();
        });
        setupBackButtonListener(view);
        return view;
    }

    /**
     * Opens the device's gallery for selecting images. This function checks
     * for permission to access media and requests permission if it's not granted. After permission is
     * granted, it launches a media picker to allow the user to select visual media.
     * <p>
     * If the required permission is already granted, the media picker is launched directly.
     * If the permission is not granted, a request for permission is initiated, and a toast message
     * is displayed to prompt the user to grant permission.
     */
    private void openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, set up the gallery picker
            pickMedia.launch(new PickVisualMediaRequest());

        } else {
            // Request storage permission if not granted.
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_GALLERY_PERMISSION);
            Toast.makeText(requireContext(), "Please grant permission to access the gallery", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up a click listener for the back button in the fragment.
     *
     * @param view The parent view containing the back button.
     */
    public void setupBackButtonListener(View view) {
        final Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
    }

    /**
     * Retrieves barcodes from an image specified by the provided Uri. The method rotates the image
     * 3 times and returns the set of barcodes from the rotation that yields the most detected barcodes.
     *
     * @param uri The Uri of the image.
     * @return A SparseArray of Barcode objects representing the detected barcodes.
     * @throws RuntimeException If an IOException occurs during image retrieval.
     */
    public SparseArray<Barcode> getBarcode(Uri uri) {
        Bitmap bitmap = null;
        try {
            if (getActivity() != null) {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            } else {
                // Use testActivity's content resolver if getActivity() is null (for testing purposes).
                bitmap = MediaStore.Images.Media.getBitmap(testActivity.getContentResolver(), uri);
            }
        } catch (IOException e) {
            // If an IOException occurs during image retrieval, throw a RuntimeException.
            throw new RuntimeException(e);
        }

        // Perform barcode detection on the original image
        SparseArray<Barcode> barcodes = detectBarcodes(bitmap);

        return barcodes;
    }

    /**
     * Check and process detected barcodes from a SparseArray of Barcode objects.
     * Performs barcode detection on the given Bitmap.
     *
     * @param bitmap The Bitmap on which barcode detection will be performed.
     * @return A SparseArray of Barcode objects representing the detected barcodes.
     */
    private SparseArray<Barcode> detectBarcodes(Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        barcodeDetector = new BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.CODE_128 | Barcode.UPC_A | Barcode.EAN_13)
                .build();
        return barcodeDetector.detect(frame);
    }


    /**
     * Checks and processes a SparseArray of Barcode objects to display barcode information.
     *
     * @param barcodes A SparseArray of Barcode objects representing detected barcodes.
     */
    @SuppressLint("RestrictedApi")
    public void checkBarcode(SparseArray<Barcode> barcodes) {
        barcodeHandleChain.handleRequest(barcodeText, barcodes, toneGen1,
                getParentFragmentManager());
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }
}
