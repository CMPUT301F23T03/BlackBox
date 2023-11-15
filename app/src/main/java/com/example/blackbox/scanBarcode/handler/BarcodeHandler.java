package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.example.blackbox.InventoryAddFragment;
import com.example.blackbox.Item;
import com.example.blackbox.NavigationManager;
import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeHandler implements CustomHandler {
    private CustomHandler nextCustomHandler;

    @Override
    public void setNextHandler(CustomHandler customHandler) {
        this.nextCustomHandler = customHandler;
    }

    @Override
    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm) {
        if (barcodes.size() != 0) {
            nextCustomHandler.handleRequest(barcodeText, barcodes, toneGen1, fm);
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

