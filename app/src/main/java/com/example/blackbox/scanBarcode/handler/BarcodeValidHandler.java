package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;

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
            String barcodeData = barcodes.valueAt(0).displayValue;
            // EAN-13 barcode has 13 digits
            boolean isEAN = barcodeData.matches("\\d{13}");
            // UPC-A barcode usually has a length of 12 and contains only digits
            boolean isUPC = (barcodeData.length() == 12 && barcodeData.matches("\\d+"));
            // Regular expression pattern for a basic Code 128 barcode
            boolean isCode128 = barcodeData.matches("^\\p{ASCII}+$");

            if (isEAN || isUPC || isCode128){
                String barcodeType = "";
                if (isEAN) {
                    barcodeType = "EAN";
                } else if (isUPC) {
                    barcodeType = "UPC";
                } else if (isCode128) {
                    barcodeType = "Code 128";
                }
                Log.d("BarcodeDetection", "Detected barcode type: " + barcodeType);
                nextCustomHandler.handleRequest(barcodeText, barcodes, toneGen1, fm);
            } else{
                barcodeText.post(new Runnable() {
                    @Override
                    public void run() {
                        barcodeText.setText("Barcode not in EAN, UPC, or Code-128 format");
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

