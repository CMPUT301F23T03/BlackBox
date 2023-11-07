package com.example.blackbox;


import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;

import android.Manifest;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import pub.devrel.easypermissions.EasyPermissions;

public class ScanGalleryFragment extends Fragment {
    private static final String READ_MEDIA_IMAGES_PERMISSION = READ_MEDIA_IMAGES;
    private ImageView imageView;
    private Button choosePictureButton;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    imageView.setImageURI(uri);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });
    // Define a constant for the request code
    private static final int REQUEST_GALLERY_PERMISSION = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_scan_fragment, container, false);
        imageView = view.findViewById(R.id.image_view);

        choosePictureButton = view.findViewById(R.id.choose_button);
        choosePictureButton.setOnClickListener(v -> {
            openGallery();
        });
        setupBackButtonListener(view);
        return view;
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), READ_MEDIA_IMAGES_PERMISSION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, set up the gallery picker
            pickMedia.launch(new PickVisualMediaRequest());
        } else {
            // Request storage permission if not granted.
//             ActivityCompat.requestPermissions(requireActivity(), new
//                     String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_GALLERY_PERMISSION);
            String[] perms = {READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
            EasyPermissions.requestPermissions(this,"Please grant permission",
                   REQUEST_GALLERY_PERMISSION, perms);
            Toast.makeText(requireContext(), "Please grant permission to access the gallery", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up a click listener for the back button in the fragment.
     * @param view  The parent view containing the back button.
     */
    public void setupBackButtonListener(View view){
        final Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            ScanFragment scanFragment = new ScanFragment();
            NavigationManager.switchFragment(scanFragment, getParentFragmentManager());
        });
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[]
//            permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
//
//        // Forward results to EasyPermissions
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
}
