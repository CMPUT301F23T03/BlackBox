package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeHandleChain {
    private CustomHandler h1;
    public BarcodeHandleChain(){
        this.h1 = new BarcodeHandler();
        CustomHandler databaseHandler = new DatabaseHandler();
        CustomHandler serialNumberHandler = new SerialNumberHandler();

        h1.setNextHandler(databaseHandler);
        databaseHandler.setNextHandler(serialNumberHandler);
    }

    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm){
        h1.handleRequest(barcodeText, barcodes, toneGen1, fm);
    }
}
