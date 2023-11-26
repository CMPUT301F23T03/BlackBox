package com.example.blackbox.tag;

import android.util.Log;

import com.example.blackbox.inventory.InventoryDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
public class TagDB {
    private CollectionReference tags;
    private FirebaseFirestore db;
    private GoogleAuthDB googleAuthDB;

    /**
     * Initializes the Firestore database and the 'tag' collection reference.
     */
    public TagDB() {
        db = FirebaseFirestore.getInstance();
        tags = db.collection("tags");
        googleAuthDB = new GoogleAuthDB();
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
        data.put("user_id", tag.getUserID());
        return data;
    }

    /**
     * Delete a specified tag from the database
     * @param tag
     *      The tag to be deleted
     */
    public void deleteTag(Tag tag){
        if (tag.getDataBaseID() != null) {
            InventoryDB itemDB = new InventoryDB();
            Task getItems = itemDB.getInventory().get();
            getItems.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(Task<QuerySnapshot> task) {
                       if (task.isSuccessful()){
                           Log.d("Firestore", "Items retrieved successfully");

                           deleteTagFromItems(tag, task);
                       }
                       else {
                           Log.d("Firestore", "Tag Deletion failed, items could not be fetched");
                       }
                   }
               });
        }
        else{
            Log.d("Firestore", "Deletion failed, tag has no ID specified");
        }
    }


    /**
     * Delete a specified tag from a list of items provided as a Query Snapshot
     * @param tag
     *      The tag to remove
     * @param task
     *      The QuerySnapshot to retrieve items from
     */
    public void deleteTagFromItems(Tag tag, Task<QuerySnapshot> task){
        String tagID = tag.getDataBaseID();
        List<Task<Void>> updateTasks = new ArrayList<>();
        for (QueryDocumentSnapshot doc : task.getResult()) {
            DocumentReference docRef = doc.getReference();
            // Get the current tag_ids field
            List<String> tagIDs = (List<String>) doc.get("tags");
            Log.d("Firestore", "Removing tag from item");
            tagIDs.remove(tagID);

            // Update the document with the modified tag_ids list
            Task updateTask = docRef.update("tags", tagIDs);
            updateTasks.add(updateTask);
        }
        Tasks.whenAllComplete(updateTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(Task<List<Task<?>>> task) {
                // All update tasks are completed
                if (task.isSuccessful()) {
                    // finally delete tag itself
                    tags.document(tag.getDataBaseID()).delete();
                } else {
                    Log.d("Firestore", "Error updating items, could not delete tag");
                }
            }
        });
    }

    public void getAllTags(OnGetTagsCallback callback) {
        tags.whereEqualTo("user_id",googleAuthDB.getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Tag> tagList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String description = document.getString("description");
                        int color = document.getLong("color").intValue();
                        String colorName = document.getString("colorName");
                        String dataBaseID = document.getId();
                        String userID = document.getString("user_id");

                        Tag tag = new Tag(name, color, colorName, description, dataBaseID, userID);
                        tagList.add(tag);
                    }
                    callback.onSuccess(tagList);
                })
                .addOnFailureListener(e -> {
                    Log.e("TagDB", "Error retrieving tags: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    public interface OnGetTagsCallback {
        void onSuccess(ArrayList<Tag> tagList);

        void onError(String errorMessage);
    }
}

