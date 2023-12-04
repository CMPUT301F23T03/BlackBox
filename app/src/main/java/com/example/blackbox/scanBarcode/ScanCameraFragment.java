package com.example.blackbox.scanBarcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.blackbox.MainActivity;
import com.example.blackbox.R;
import com.example.blackbox.scanBarcode.handler.BarcodeHandleChain;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


/**
 * This class represents a Fragment for scanning barcodes using the device's camera.
 * It returns the serial number of the barcode. If successful, it will give a sound.
 */
public class ScanCameraFragment extends Fragment {
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private BarcodeHandleChain barcodeHandleChain;

    /**
     * Inflates the layout for the barcode scanning fragment and initializes necessary components.
     * @param inflater       LayoutInflater to inflate the layout.
     * @param container      ViewGroup that the fragment is attached to.
     * @param savedInstanceState  Bundle for saved instance state.
     * @return  The created view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // disable navigation bar
        ((MainActivity) requireActivity()).toggleBottomNavigationView(false);


        View view = inflater.inflate(R.layout.camera_scan_fragment, container, false);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        barcodeHandleChain = new BarcodeHandleChain();
        surfaceView = view.findViewById(R.id.surface_view);
        barcodeText = view.findViewById(R.id.barcode_text);

        setupBackButtonListener(view);
        initializeDetectorsAndSources();

        return view;
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

    /**
     * Initializes barcode detection and camera source for scanning.
     */
    private void initializeDetectorsAndSources() {
        // Initialize a barcode detector with all barcode formats.
        barcodeDetector = new BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        // Set up a callback for the SurfaceView to handle surface-related events.
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // Initialize a camera source using the barcode detector and configure it.
                startCameraSource();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Handle surface changes if needed.
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // Stop the camera source when the surface is destroyed.
                cameraSource.stop();
            }
        });


        // Set up a processor to handle barcode detection results.
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Release resources when barcode processing is stopped.
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                barcodeHandleChain.handleRequest(barcodeText, barcodes, toneGen1,
                        getParentFragmentManager());
            }
        });
    }

    /**
     * Start the camera source for capturing images or video.
     * This method is typically called when the camera permission is granted.
     */
    private void startCameraSource() {
        try {
            // Check if the camera permission is granted.
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                // Camera permission is granted, start the camera source.
                cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                        .setRequestedPreviewSize(1920, 1080)
                        .setAutoFocusEnabled(true)
                        .build();
                cameraSource.start(surfaceView.getHolder());
                barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                    @Override
                    public void release() {
                        // Release resources when barcode processing is stopped.
                    }

                    @Override
                    public void receiveDetections(Detector.Detections<Barcode> detections) {
                        final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                        barcodeHandleChain.handleRequest(barcodeText, barcodes, toneGen1,
                                getParentFragmentManager());
                    }
                });
            } else {
                // Camera permission is not granted, request it from the user.
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        } catch (IOException e) {
            // Handle an exception if there's an issue starting the camera source.
            e.printStackTrace();
        }
    }


    /**
     * Called when the fragment is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (((AppCompatActivity)getActivity()).getSupportActionBar()!= null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }
        cameraSource.release();
    }

    /**
     * Called when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }
        initializeDetectorsAndSources();
    }


    /**
     * Override method called when the app requests permissions at runtime,
     * and the user responds to the permission request.
     *
     * @param requestCode   The request code passed to requestPermissions().
     * @param permissions   The requested permissions. This array can contain one or more permissions.
     * @param grantResults  The grant results for the corresponding permissions in the permissions array.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Call the super method to ensure proper handling of permission results.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the permission request code matches the camera permission request code.
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // Check if the camera permission is granted.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed to start the camera source.
                startCameraSource();
            } else {
                // Camera permission denied, handle accordingly (e.g., show a message to the user).
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
