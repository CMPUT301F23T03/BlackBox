package com.example.blackbox.scanBarcode.handler;

import android.media.ToneGenerator;
import android.util.Log;
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

    private String getAttributeValue(Element element, String query) {
        try {
            Element selectedElement = element.select(query).first();
            if (selectedElement != null) {
                if (selectedElement.tagName().equalsIgnoreCase("textarea")) {
                    return selectedElement.text(); // Retrieve content of the textarea
                } else {
                    return selectedElement.attr("value"); // Retrieve value attribute of the input
                }
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
        return null;
    }


    @Override
    public void handleRequest(TextView barcodeText, SparseArray<Barcode> barcodes,
                              ToneGenerator toneGen1, FragmentManager fm)  {
        String barcodeData = barcodes.valueAt(0).displayValue;
        // perform web-scraping
        String url = "https://www.barcodelookup.com/" + barcodeData;

        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
            // Get the meta description element
            Element metaDescription = doc.selectFirst("meta[name=description]");
            String descriptionContent = metaDescription.attr("content");
            Log.d("Web scrape", "Web description content " + descriptionContent);
            // Check if product is found in online database
            if (descriptionContent.contains("This Product doesn't exist in our database.")) {
                newItem = new Item(null, null, null,
                        null, null, null, barcodeData,
                        null, null);
            } else {
                String name = getAttributeValue(doc, "input[name=title]");
                String description = getAttributeValue(doc, "textarea[name=description]");
                String make = getAttributeValue(doc, "input[name=manufacturer]");
                newItem = new Item(name, null, null,
                        null, make, null, barcodeData,
                        description, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Web scrape", "Fail");
            newItem = new Item(null, null, null,
                    null, null, null, barcodeData,
                    null, null);
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
