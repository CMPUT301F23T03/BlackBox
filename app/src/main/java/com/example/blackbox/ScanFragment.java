package com.example.blackbox;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * The `ScanFragment` class represents a fragment for initiating barcode scanning operations.
 * It provides buttons to access the device camera for live barcode scanning and to browse
 * the device gallery for barcode images.
 */
public class ScanFragment extends Fragment {
    private View view;  // The fragment's view

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.scan_fragment, container, false);

        // Camera button
        final Button cameraButton = view.findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the camera button is clicked. Switches to the `ScanCameraFragment`
             * for live barcode scanning using the device camera.
             *
             * @param v The view that was clicked.
             */
            public void onClick(View v) {
                NavigationManager.switchFragment(new ScanCameraFragment(), getParentFragmentManager());
            }
        });

        // Gallery button
        final Button galleryButton = view.findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the gallery button is clicked. Switches to the `ScanGalleryFragment`
             * for browsing and selecting barcode images from the device gallery.
             *
             * @param v The view that was clicked.
             */
            public void onClick(View v) {
                NavigationManager.switchFragment(new ScanGalleryFragment(), getParentFragmentManager());
            }
        });

        return view;
    }
}
