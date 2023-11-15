package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.example.blackbox.InventoryAddFragment;
import com.example.blackbox.Item;
import com.example.blackbox.NavigationManager;
import com.google.android.gms.vision.barcode.Barcode;

public class SerialNumberHandler implements CustomHandler {
    private CustomHandler nextCustomHandler;

    @Override
    public void setNextHandler(CustomHandler customHandler) {
        this.nextCustomHandler = customHandler;
    }

    @Override
    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm) {
        if (true) {
            barcodeText.post(new Runnable() {
                @Override
                public void run() {
                    // Display the barcode content and play a tone.
                    String barcodeData = barcodes.valueAt(0).displayValue;
                    barcodeText.setText(barcodeData);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                    Item newItem = new Item(null, null, null,
                            null, null, null, barcodeData,
                            null, null);
                    InventoryAddFragment invFrag = InventoryAddFragment.newInstance(newItem);
                    NavigationManager.switchFragment(invFrag, fm);
                }
            });
        } else if (nextCustomHandler != null) {
            nextCustomHandler.handleRequest(barcodeText, barcodes, toneGen1, fm);
        } else {
            System.out.println("Unknown serial number type");
        }
    }
}
