package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;

public class DatabaseHandler implements CustomHandler{
    private CustomHandler nextCustomHandler;
    @Override
    public void setNextHandler(CustomHandler customHandler) {
        this.nextCustomHandler = customHandler;
    }

    @Override
    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm) {
        nextCustomHandler.handleRequest(barcodeText, barcodes, toneGen1, fm);
    }
}
