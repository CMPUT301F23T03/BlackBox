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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
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
     * Adds a new tag to the 'tags' collection in the Firestore database.
     *
     * @param tag The tag object to be added to the database.
     */
    public void addTagToDB(Tag tag) {
        Map<String, Object> data = generateTagHashMap(tag);
        // Add the item data to the Firestore collection
        tags.add(data);
    }

    /**
     * Updates a tag in the 'tags' collection in the Firestore database.
     *
     * @param oldTag
     *      The tag object to be updated.
     * @param newTag
     *      The tag object to take new info from.
     */
    public void editTag(Tag oldTag, Tag newTag) {
        Map<String, Object> data = generateTagHashMap(newTag);
        if (oldTag.getDataBaseID() != null) {
            tags.document(oldTag.getDataBaseID()).update(data);
            Log.d("Firestore", "Updated Successfully");
        }
        else{
            Log.d("Firestore", "Update failed, no tag to update specified");
        }

    }

    /**
     * Creates a HashMap which represents the data of a tag
     * @param tag
     *      The tag to create a HashMap from
     */
    private Map<String, Object> generateTagHashMap(Tag tag){
        Map<String, Object> data = new HashMap<>();
        data.put("name", tag.getName());
        data.put("color", tag.getColor());
        data.put("description", tag.getDescription());
        data.put("color_name", tag.getColorName());
        // note the date of the tag
        tag.setDateUpdated(Calendar.getInstance().getTime());
        data.put("update_date", tag.getStringDateUpdated());
        return data;
    }

    /**
     * Delete a specified tag from the database
     * @param tag
     *      The tag to be deleted
     */
    public void deleteTag(Tag tag){
        if (tag.getDataBaseID() != null) {
            tags.document(tag.getDataBaseID()).delete();
            Log.d("Firestore", "Tag deleted Successfully");
        }
        else{
            Log.d("Firestore", "Deletion failed, tag has no ID specified");
        }
    }

    public void getAllTagNames(OnGetTagNamesCallback callback) {
        tags.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> tagNames = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String tagName = document.getString("name");
                        if (tagName != null) {
                            tagNames.add(tagName);
                        }
                    }
                    callback.onSuccess(tagNames);
                })
                .addOnFailureListener(e -> {
                    Log.e("TagDB", "Error retrieving tag names: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    public interface OnGetTagNamesCallback {
        void onSuccess(ArrayList<String> tagList);

        void onError(String errorMessage);
    }
}

