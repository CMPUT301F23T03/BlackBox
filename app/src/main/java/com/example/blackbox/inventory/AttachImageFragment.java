package com.example.blackbox.inventory;

import static android.Manifest.permission.READ_MEDIA_IMAGES;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.blackbox.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * AttachImageFragment is responsible for handling image attachment functionalities.
 * It allows users to capture images from the camera or select images from the gallery.
 * Selected images can be confirmed for attachment and displayed in an ImageView.
 * Additionally, it provides a listener interface to notify when an image is selected.
 */
public class AttachImageFragment extends Fragment {
    // UI elements
    private View view;
    private ImageView imageView;
    private Button cameraButton;
    private Button galleryButton;
    private Button confirmButton;

    // Constants for permission requests
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final int REQUEST_GALLERY_PERMISSION = 200;

    // Activity Result Launchers and related variables
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Uri imageUri;
    private File cameraImageFile;
    private Uri cameraImageUri;
    private ActivityResultLauncher<Uri> takePicture;
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    // Listener for image selection
    private OnImageSelectedListener imageSelectedListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        view = inflater.inflate(R.layout.attach_image_fragment, container, false);
        imageView = view.findViewById(R.id.image_view);
        cameraButton = view.findViewById(R.id.camera_button);
        galleryButton = view.findViewById(R.id.gallery_button);
        confirmButton = view.findViewById(R.id.confirm_button);

        // Search picture in gallery
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                imageView.setImageURI(uri);
                imageUri = uri;
                Log.d("PhotoPicker", "Selected URI: " + uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        // Creating a temporary camera image file and obtaining its URI
        cameraImageFile = CreateTempImage();
        cameraImageUri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".provider", cameraImageFile);

        // Activity Result Launcher for capturing an image and store it in side imageFile
        takePicture = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        if (cameraImageUri != null) {
                            imageView.setImageURI(null); // reset view to null
                            imageView.setImageURI(cameraImageUri); // attach new image
                            imageUri = cameraImageUri;
                            Log.d("Camera", "Selected URI: " + imageUri);
                        } else {
                            Log.d("Camera", "No media selected");
                        }
                    }
                });

        setupBackButtonListener(view);
        initializeCamera();
        initializeGallery();
        confirmAttachment();

        return view;
    }

    public ArrayList<Uri> getUriArrayList(){
        return uriArrayList;
    }

    // Setter method for the listener
    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.imageSelectedListener = listener;
    }

    /**
     * Creates a temporary image file with a timestamp in the directory
     * obtained from external files directory in the Pictures directory.
     * @return File object representing the created image file.
     */
    private File CreateTempImage() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "temp" + timeStamp;
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName + ".jpg");
        return imageFile;
    }

    /**
     * Initializes the camera functionality if the CAMERA permission is granted.
     * If the permission is not granted, requests the CAMERA permission.
     * Sets an OnClickListener on the cameraButton to launch the takePicture action.
     */
    private void initializeCamera(){
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}
                    , REQUEST_CAMERA_PERMISSION);
        }
        cameraButton.setOnClickListener(v -> {
            takePicture.launch(cameraImageUri);
        });
    }

    /**
     * Initializes the gallery functionality if the READ_MEDIA_IMAGES permission is granted.
     * If the permission is not granted, requests the READ_MEDIA_IMAGES permission.
     * Sets an OnClickListener on the galleryButton to launch the pickMedia action.
     */
    private void initializeGallery(){
        if (ContextCompat.checkSelfPermission(requireContext(), READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{READ_MEDIA_IMAGES}
                    , REQUEST_GALLERY_PERMISSION);
        }
        galleryButton.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest()));
    }


    public interface OnImageSelectedListener {
        void onImageSelected(Uri imageUri);
    }

    /**
     * Handles confirmation of attachment by setting an OnClickListener on the confirmButton.
     * Displays Snackbar error messages for various scenarios:
     * - If imageView is empty, prompts to select an image.
     * - If the selected image is already added, displays a message indicating the image is already added.
     * Adds the imageUri to the uriArrayList if it's not a duplicate.
     * Notifies the imageSelectedListener about the selected image and navigates back from the fragment.
     */
    private void confirmAttachment() {
        confirmButton.setOnClickListener(v -> {
            if (imageView.getDrawable() == null) {
                // Display Snackbar error message when imageView is empty
                Snackbar.make(view, "Please select an image first", Snackbar.LENGTH_SHORT).show();
            } else if (isUriDuplicate(uriArrayList, imageUri)) {
                // Check for duplicates before adding
                Snackbar.make(view, "Image already added", Snackbar.LENGTH_SHORT).show();
            } else {
                uriArrayList.add(imageUri);
                if (imageSelectedListener != null) {
                    imageSelectedListener.onImageSelected(imageUri);
                }
                getParentFragmentManager().popBackStack();
            }
        });
    }

    /** Function to check if a URI already exists in the ArrayList
     *
     * @param uriList
     * @param uriToCheck
     * @return boolean
     */
    private static boolean isUriDuplicate(ArrayList<Uri> uriList, Uri uriToCheck) {
        for (Uri uri : uriList) {
            if (uri.equals(uriToCheck)) {
                return true; // URI already exists in the list
            }
        }
        return false; // URI does not exist in the list
    }



    /**
     * Sets up a click listener for the back button in the fragment.
     * @param view  The parent view containing the back button.
     */
    public void setupBackButtonListener(View view){
        final Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
    }
}
