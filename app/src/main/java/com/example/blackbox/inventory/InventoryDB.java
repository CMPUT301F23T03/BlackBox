package com.example.blackbox.inventory;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.blackbox.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private CollectionReference images;
    private StorageReference imagesStorageReference;
    private FirebaseFirestore db;

    /**
     * Initializes the Firestore database and the 'inventory' collection reference.
     */
    public InventoryDB() {
        db = FirebaseFirestore.getInstance();
        inventory = db.collection("inventory");
        images = db.collection("images");

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        imagesStorageReference = storage.getReference().child("images");
    }

    /**
     * Initializes the Firestore database with a provided collection name
     * Used for testing
     * @param collection_name name of the collection to be created
     */
    public InventoryDB(String collection_name) {
        db = FirebaseFirestore.getInstance();
        inventory = db.collection(collection_name);
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
     * Adds a new images to the 'images' collection in the Firestore database.
     *
     * @param imageList The Uri objects to be added to the database.
     */
    public void addImagesToDB(ArrayList<Uri> imageList) {
        for (Uri imageUri : imageList) {
            uploadImageToStorage(imageUri);
        }
    }

    /**
     * Uploads an image to Firebase Storage.
     *
     * @param imageUri The Uri object representing the image to be uploaded.
     */
    private void uploadImageToStorage(Uri imageUri) {
        // Create a reference to store images in Firebase Storage with a unique name
        String imageName = System.currentTimeMillis() + "_" + imageUri.getLastPathSegment();
        StorageReference imageRef = imagesStorageReference.child(imageName);

        // Upload the file to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        // Get the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Image uploaded successfully, now add its URL to Firestore
                                addImageUrlToFirestore(downloadUri);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful uploads
                        Log.d("Fail image upload", "Failed to upload image to Storage: " + e.getMessage());
                    }
                });
    }

    /**
     * Adds an image URL to the 'images' collection in the Firestore database.
     *
     * @param downloadUri The download Uri representing the image URL.
     */
    private void addImageUrlToFirestore(Uri downloadUri) {
        // Create a map to store the image URL
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imageUrl", downloadUri.toString()); // Convert Uri to String

        // Add the image URL to Firestore
        images.add(imageData)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("Success image add", "Image URL added to Firestore: " + downloadUri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Fail image add", "Failed to add image URL to Firestore: " + e.getMessage());
                    }
                });
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
        data.put("purchase_date", item.getDateOfPurchase());
        item.setDateUpdated(Calendar.getInstance().getTime());
        data.put("update_date", item.getStringDateUpdated());
        data.put("user_id", item.getUserID());

        // Convert the list of tags to a list of tag IDs
        List<String> tagIDs = new ArrayList<>();
        if (item.getTags() != null) {
            for (Tag tag : item.getTags()) {
                tagIDs.add(tag.getDataBaseID());
            }
        }
        data.put("tags", tagIDs);

        return data;
    }

    /**
     * Delete a specified tag from the database
     * @param item
     *      The item to be deleted
     */
    public void deleteItem(Item item){
        if (item.getID() != null) {
            inventory.document(item.getID()).delete();
            Log.d("Firestore", "Item deleted Successfully");
        }
        else{
            Log.d("Firestore", "Deletion failed, item has no ID specified");
        }
    }

    /**
     * Clears all items in the 'inventory' collection in the Firestore database.
     */
    public void clearInventory() {
        inventory.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                    Log.d("Firestore", "Inventory cleared Successfully");

                } else {
                    Log.d("Firestore", "Failed to clear inventory", task.getException());
                }
            }
        });
    }
}

