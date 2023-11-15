package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.example.blackbox.InventoryAddFragment;
import com.example.blackbox.Item;
import com.example.blackbox.NavigationManager;
import com.google.android.gms.vision.barcode.Barcode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebScrapingHandler implements CustomHandler {
    private CustomHandler nextCustomHandler;
    private Item newItem;

    @Override
    public void setNextHandler(CustomHandler customHandler) {
        this.nextCustomHandler = customHandler;
    }

    @Override
    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm)  {
        String barcodeData = barcodes.valueAt(0).displayValue;
        // perform web-scraping
        String url = "https://www.barcodelookup.com/" + barcodeData;

        try {
            Document doc = Jsoup.connect(url).get();
            // Get the meta description element
            Element metaDescription = doc.selectFirst("meta[name=description]");
            String descriptionContent = metaDescription.attr("content");
            // Check if product is found in online database
            if (descriptionContent.contains("This Product doesn't exist in our database.")) {
                newItem = new Item(null, null, null,
                        null, null, null, barcodeData,
                        null, null);
            } else {
                newItem = new Item(null, null, null,
                        null, null, null, barcodeData,
                        null, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        barcodeText.post(new Runnable() {
            @Override
            public void run() {
                // Display the barcode content and play a tone.
                barcodeText.setText(barcodeData);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                InventoryAddFragment invFrag = InventoryAddFragment.newInstance(newItem);
                NavigationManager.switchFragment(invFrag, fm);
            }
        });
    }
}
