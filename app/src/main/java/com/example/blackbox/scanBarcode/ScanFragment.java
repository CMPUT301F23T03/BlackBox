package com.example.blackbox.scanBarcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.blackbox.AttachImageFragment;
import com.example.blackbox.NavigationManager;
import com.example.blackbox.R;

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
        view =  inflater.inflate(R.layout.scan_fragment, container, false);

        // Camera button
        final Button cameraButton = view.findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationManager.switchFragmentWithBack(new ScanCameraFragment(), getParentFragmentManager());
            }
        });

        // Gallery button
        final Button galleryButton = view.findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationManager.switchFragmentWithBack(new ScanGalleryFragment(), getParentFragmentManager());
            }
        });

        // Gallery button
        final Button photoButton = view.findViewById(R.id.button_photo);
        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationManager.switchFragmentWithBack(new AttachImageFragment(), getParentFragmentManager());
            }
        });

        return view;
    }
}
