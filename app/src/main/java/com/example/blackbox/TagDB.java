package com.example.blackbox;

import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles interactions with the Firestore database for managing inventory items.
 */
public class TagDB {
    private CollectionReference tags;
    private FirebaseFirestore db;

    /**
     * Initializes the Firestore database and the 'inventory' collection reference.
     */
    public TagDB() {
        db = FirebaseFirestore.getInstance();
        tags = db.collection("tags");
    }

    /**
     * Get the tag collection
     * @return tags a CollectionReference object
     */
    public CollectionReference getTags() {
        return tags;
    }


    /**
     * Adds a new item to the 'inventory' collection in the Firestore database.
     *
     * @param tag The tag object to be added to the database.
     */
    public void addTagToDB(Tag tag) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", tag.getName());
        data.put("color", tag.getColor());
        data.put("description", tag.getDescription());
        // Add the item data to the Firestore collection
        tags.add(data);
    }
}

