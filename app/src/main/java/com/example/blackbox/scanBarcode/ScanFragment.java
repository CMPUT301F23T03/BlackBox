package com.example.blackbox.scanBarcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.blackbox.MainActivity;
import com.example.blackbox.utils.NavigationManager;
import com.example.blackbox.R;

/**
 * The `ScanFragment` class represents a fragment for initiating barcode scanning operations.
 * It provides buttons to access the device camera for live barcode scanning and to browse
 * the device gallery for barcode images.
 */
public class ScanFragment extends Fragment {
    private View view;  // The fragment's view

    /**
     * A method to which sets up the fragment
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.scan_fragment, container, false);

        // enable navigation bar
        ((MainActivity) requireActivity()).toggleBottomNavigationView(true);

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

        return view;
    }
}
