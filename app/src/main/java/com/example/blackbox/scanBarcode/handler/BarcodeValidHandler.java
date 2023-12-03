package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;
/**
 * Implementation of CustomHandler used for validating barcode formats.
 * This handler checks the validity of received barcodes and allows processing for specific barcode types.
 */
public class BarcodeValidHandler implements CustomHandler {
    private CustomHandler nextCustomHandler;

    @Override
    public void setNextHandler(CustomHandler customHandler) {
        this.nextCustomHandler = customHandler;
    }

    @Override
    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm) {
        // Check barcode validity
        if (barcodes.size() != 0) {
            Barcode barcode = barcodes.valueAt(0);

            if (barcode.format == Barcode.CODE_128 ||
                    barcode.format == Barcode.UPC_A ||
                    barcode.format == Barcode.EAN_13){
                String barcodeType = "";
                switch (barcode.format) {
                    case Barcode.CODE_128:
                        barcodeType = "CODE_128";
                    case Barcode.UPC_A:
                        barcodeType = "UPC_A";
                    case Barcode.EAN_13:
                        barcodeType = "EAN_13";
                }
                Log.d("BarcodeDetection", "Detected barcode type: " + barcodeType);
                if (nextCustomHandler != null){
                    nextCustomHandler.handleRequest(barcodeText, barcodes, toneGen1, fm);
                }
            } else{
                barcodeText.post(new Runnable() {
                    @Override
                    public void run() {
                        barcodeText.setText("Incorrect Barcode format.");
                    }
                });
            }
        } else {
            barcodeText.post(new Runnable() {
                @Override
                public void run() {
                    barcodeText.setText("No Barcode is detected");
                }
            });
        }
    }

}

