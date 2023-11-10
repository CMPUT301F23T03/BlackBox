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
 * This class represents a view where a user can choose to scan a barcode with their camera
 * or choose to select a barcode from images on their phone
 */
public class ScanFragment extends Fragment{
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.scan_fragment, container, false);

        // Camera button
        final Button cameraButton = view.findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationManager.switchFragment(new ScanCameraFragment(), getParentFragmentManager());
            }
        });

        // Gallery button
        final Button galleryButton = view.findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationManager.switchFragment(new ScanGalleryFragment(), getParentFragmentManager());
            }
        });

        return view;
    }
}
