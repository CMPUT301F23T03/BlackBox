package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeHandleChain {
    private CustomHandler h1;
    public BarcodeHandleChain(){
        this.h1 = new BarcodeValidHandler();
        CustomHandler databaseHandler = new DatabaseHandler();
        CustomHandler webScrapingHandler = new WebScrapingHandler();

        h1.setNextHandler(databaseHandler);
        databaseHandler.setNextHandler(webScrapingHandler);
    }

    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm){
        h1.handleRequest(barcodeText, barcodes, toneGen1, fm);
    }
}
