package com.example.blackbox;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
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
     * Returns the instance of the Firestore database
     *
     * @return db
     */
    public FirebaseFirestore getDb() {
        return db;
    }


    /**
     * Gets the inventory collection
     * @return inventory    a CollectionReference object
     */
    public CollectionReference getInventory() {
        return inventory;
    }

    /**
     * Adds a new item to the 'inventory' collection in the Firestore database.
     *
     * @param item The Item object to be added to the database.
     */
    public void addItemToDB(Item item) {
        Map<String, Object> data = generateItemHashMap(item);
        inventory.add(data);
    }

    /**
     * Updates an item in the Firestore database with new values
     *
     * @param old_item  The item to be replaced.
     * @param new_item  The item from which the new values should come.
     */
    public void updateItemInDB(Item old_item, Item new_item) {
        Map<String, Object> data = generateItemHashMap(new_item);
        inventory.document(old_item.getID()).set(data);
    }

    /**
     * Creates a HashMap which represents the data of a tag
     * @param item
     *      The item to create a HashMap from
     */
    private Map<String, Object> generateItemHashMap(Item item){
        Map<String, Object> data = new HashMap<>();
        data.put("name", item.getName());
        data.put("value", item.getEstimatedValue());
        data.put("description", item.getDescription());
        data.put("make", item.getMake());
        data.put("model", item.getModel());
        data.put("serial_number", item.getSerialNumber());
        data.put("comment", item.getComment());
        item.setDateUpdated(Calendar.getInstance().getTime());
        data.put("update_date", item.getStringDateUpdated());

        // Convert the list of tags to a list of tag IDs
        List<String> tagIDs = new ArrayList<>();
        for (Tag tag : item.getTags()) {
            tagIDs.add(tag.getDataBaseID());
        }
        data.put("tags", tagIDs);

        return data;
    }
}

