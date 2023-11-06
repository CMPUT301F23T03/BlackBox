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

public class ScanFragment extends Fragment{
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.scan_fragment, container, false);

        final Button cameraButton = view.findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Scanning shit", Toast.LENGTH_LONG).show();

                // Create an intent to open the camera app
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Check if there's a camera app available
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Start the camera app
                    startActivity(takePictureIntent);
                }
            }
        });

        final Button galleryButton = view.findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Scanning shit", Toast.LENGTH_LONG).show();

            }
        });

        return view;
    }
}
