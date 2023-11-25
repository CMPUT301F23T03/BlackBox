package com.example.blackbox;

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
    public ArrayList<Uri> uriArrayList = new ArrayList<>();
    private File imageFile;
    private ActivityResultLauncher<Uri> takePicture;

    // Listener for image selection
    private OnImageSelectedListener imageSelectedListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View view = inflater.inflate(R.layout.attach_image_fragment, container, false);
        imageView = view.findViewById(R.id.image_view);
        cameraButton = view.findViewById(R.id.camera_button);
        galleryButton = view.findViewById(R.id.gallery_button);
        confirmButton = view.findViewById(R.id.confirm_button);

        // Search picture in gallery
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                imageView.setImageURI(uri);
                Log.d("PhotoPicker", "Selected URI: " + uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        // Creating a temporary image file and obtaining its URI
        imageFile = CreateTempImage();
        imageUri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".provider", imageFile);

        // Activity Result Launcher for capturing an image and store it in side imageFile
        takePicture = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            if (imageUri != null) {
                                imageView.setImageURI(imageUri);
                                Log.d("Camera", "Selected URI: " + imageUri);
                            } else {
                                Log.d("Camera", "No media selected");
                            }
                        }
                    }
                });

        setupBackButtonListener(view);
        initializeCamera();
        initializeGallery();
        confirmAttachment();

        return view;
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

    private void initializeCamera(){
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}
                    , REQUEST_CAMERA_PERMISSION);
        }
        cameraButton.setOnClickListener(v -> {
            takePicture.launch(imageUri);
        });
    }

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

    private void confirmAttachment(){
        confirmButton.setOnClickListener(v -> {
            uriArrayList.add(imageUri);
            if (imageSelectedListener != null) {
                imageSelectedListener.onImageSelected(imageUri);
            }
            getParentFragmentManager().popBackStack();
        });
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
