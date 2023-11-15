package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseHandler implements CustomHandler{
    private CustomHandler nextCustomHandler;
    private CollectionReference inventory;
    @Override
    public void setNextHandler(CustomHandler customHandler) {
        this.nextCustomHandler = customHandler;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        inventory = db.collection("inventory");
    }

    @Override
    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm) {
        nextCustomHandler.handleRequest(barcodeText, barcodes, toneGen1, fm);
    }
}
