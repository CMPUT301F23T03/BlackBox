package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.vision.barcode.Barcode;
/**
 * Class representing a chain of responsibility for handling barcode requests.
 * It initializes a chain of CustomHandlers and delegates barcode requests through the chain.
 */
public class BarcodeHandleChain {
    private CustomHandler h1;

    /**
     * Constructs a BarcodeHandleChain and initializes a chain of CustomHandlers:
     * BarcodeValidHandler, DatabaseHandler, and WebScrapingHandler.
     * Establishes the chain of responsibility by setting next handlers accordingly.
     */
    public BarcodeHandleChain(){
        this.h1 = new BarcodeValidHandler();
        CustomHandler databaseHandler = new DatabaseHandler();
        CustomHandler webScrapingHandler = new WebScrapingHandler();

        h1.setNextHandler(databaseHandler);
        databaseHandler.setNextHandler(webScrapingHandler);
    }

    /**
     * Handles the barcode request by initiating the request through the chain of handlers.
     *
     * @param barcodeText TextView to display messages related to barcode detection.
     * @param barcodes    SparseArray containing Barcode data.
     * @param toneGen1    ToneGenerator for playing a tone.
     * @param fm          FragmentManager for handling fragments.
     */
    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm){
        h1.handleRequest(barcodeText, barcodes, toneGen1, fm);
    }
}
