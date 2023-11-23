package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;

import java.net.MalformedURLException;

public interface CustomHandler {
    void setNextHandler(CustomHandler customHandler);

    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm);

}
