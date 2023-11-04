package com.example.blackbox;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles interactions with the Firestore database for managing inventory items.
 */
public class InventoryDB {
    private CollectionReference inventory;
    private FirebaseFirestore db;

    /**
     * Initializes the Firestore database and the 'inventory' collection reference.
     */
    public InventoryDB() {
        db = FirebaseFirestore.getInstance();
        inventory = db.collection("inventory");
    }

    /**
     * Adds a snapshot listener to the 'inventory' collection, updating the provided data list and adapter
     * when changes occur in the Firestore database.
     *
     * @param dataList         The list of Item objects to be updated based on changes in the database.
     * @param inventoryAdapter The ArrayAdapter used for displaying items in the UI.
     */
    public void addSnapShotListener(ArrayList<Item> dataList, ArrayAdapter<Item> inventoryAdapter) {
        inventory.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle any errors or exceptions
                    return;
                }
                dataList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    String name = doc.getString("name");
                    String val = doc.getString("value");
                    String desc = doc.getString("description");
                    dataList.add(new Item(name, Integer.parseInt(val), desc));
                }
                // Notify the adapter that the data has changed
                inventoryAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Adds a new item to the 'inventory' collection in the Firestore database.
     *
     * @param item The Item object to be added to the database.
     */
    public void addItemToDB(Item item) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", item.getName());
        data.put("value", item.getEstimatedValue());
        data.put("description", item.getDescription());
        // Add the item data to the Firestore collection
        inventory.add(data);
    }
}

