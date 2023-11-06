package com.example.blackbox;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
    private String barcodeData;

    /**
     * Inflates the layout for the barcode scanning fragment and initializes necessary components.
     * @param inflater       LayoutInflater to inflate the layout.
     * @param container      ViewGroup that the fragment is attached to.
     * @param savedInstanceState  Bundle for saved instance state.
     * @return  The created view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_scan_fragment, container, false);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
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
            ScanFragment scanFragment = new ScanFragment();
            NavigationManager.switchFragment(scanFragment, getParentFragmentManager());
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

        // Initialize a camera source using the barcode detector and configure it.
        cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        // Set up a callback for the SurfaceView to handle surface-related events.
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    // Check camera permission and start the camera source if granted.
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        // Request camera permission if not granted.
                        ActivityCompat.requestPermissions(requireActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                if (barcodes.size() != 0) {
                    barcodeText.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {
                                // If a barcode contains an email address, display it and play a tone.
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {
                                // Display the barcode's content and play a tone.
                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            }
                        }
                    });
                }
            }
        });
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
}
