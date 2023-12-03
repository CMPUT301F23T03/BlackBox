package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;

import java.net.MalformedURLException;
/**
 * Interface defining the structure for handling barcode requests in a chain of responsibility.
 * Implementing classes are responsible for processing barcode requests based on specific criteria.
 */
public interface CustomHandler {
    /**
     * Sets the next handler in the chain of responsibility.
     *
     * @param customHandler The next CustomHandler to be set.
     */
    void setNextHandler(CustomHandler customHandler);

    /**
     * Handles the barcode request with specific criteria.
     *
     * @param barcodeText TextView to display messages related to barcode detection.
     * @param barcodes    SparseArray containing Barcode data.
     * @param toneGen1    ToneGenerator for playing a tone.
     * @param fm          FragmentManager for handling fragments.
     */
     void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm);
}
