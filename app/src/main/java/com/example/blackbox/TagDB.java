package com.example.blackbox;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
     * Initializes the Firestore database and the 'tag' collection reference.
     */
    public TagDB() {
        db = FirebaseFirestore.getInstance();
        tags = db.collection("tags");
    }

    /**
     * Initializes the Firestore database with a provided collection reference.
     * Mostly used for creating test databases.
     * @param collection
     *      The name of the collection to associate with the DB
     */
    public TagDB(String collection) {
        db = FirebaseFirestore.getInstance();
        tags = db.collection(collection);
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
        data.put("color_name", tag.getColorName());
        // Add the item data to the Firestore collection
        tags.add(data);
    }

    public void editTag(Tag oldTag, Tag newTag) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", newTag.getName());
        data.put("color", newTag.getColor());
        data.put("description", newTag.getDescription());
        data.put("color_name", newTag.getColorName());
        if (oldTag.getDataBaseID() != null) {
            tags.document(oldTag.getDataBaseID()).update(data);
            Log.d("Firestore", "Updated Successfully");
        }
        else{
            Log.d("Firestore", "Update failed, no tag to update specified");
        }

    }

    public void deleteTag(Tag tag){
        if (tag.getDataBaseID() != null) {
            tags.document(tag.getDataBaseID()).delete();
            Log.d("Firestore", "Tag deleted Successfully");
        }
        else{
            Log.d("Firestore", "Deletion failed, no tag to update specified");
        }
    }
}

