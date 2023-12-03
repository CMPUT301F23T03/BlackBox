package com.example.blackbox.inventory;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.example.blackbox.ImageRecyclerAdapter;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Handles interactions with the Firestore database for managing inventory items.
 */
public class InventoryDB {
    private CollectionReference inventory;
    private CollectionReference images;
    private StorageReference imagesStorageReference;
    private String itemId; // collection id of the item in inventory
    private int numberOfImages = 0;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ArrayList<Uri> downloadUris;

    /**
     * Initializes the Firestore database and the 'inventory' collection reference.
     */
    public InventoryDB() {
        db = FirebaseFirestore.getInstance();
        inventory = db.collection("inventory");
        images = db.collection("images");

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
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

    public StorageReference getImagesStorageReference(){
        return imagesStorageReference;
    }

    /**
     * Adds a new item to the 'inventory' collection in the Firestore database.
     *
     * @param item The Item object to be added to the database.
     */

    public void addItemToDB(Item item) {
        Map<String, Object> data = generateItemHashMap(item);
        // Generate a custom itemId
        itemId = inventory.document().getId();
        // Add the item with the custom itemId to Firestore
        inventory.document(itemId).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Item added successfully
                        Log.d("Success", "Item added to Firestore with itemId: " + itemId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Log.e("Error", "Failed to add item to Firestore: " + e.getMessage());
                    }
                });
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
        String lastPathSegment = imageUri.getLastPathSegment();

        // rename image files
        if (lastPathSegment.endsWith(".jpg")){
            lastPathSegment = lastPathSegment.substring(0, lastPathSegment.length() - 4);
            Log.d("Last path", lastPathSegment);
        }
        String imageName = System.currentTimeMillis() + "_"
                + lastPathSegment.substring(lastPathSegment.length() - 5);
        StorageReference imageRef = imagesStorageReference.child(imageName);
        downloadUris = new ArrayList<>();
        // Upload the file to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener((OnSuccessListener) o -> {
                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Image uploaded successfully, now add its URL to Firestore
                        addImageUrlToFirestore(downloadUri);
                        downloadUris.add(downloadUri);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Log.d("Fail image upload", "Failed to upload image to Storage: " + e.getMessage());
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
        imageData.put("itemId", itemId); // attach image to item
        imageData.put("imageUrl", downloadUri.toString()); // Convert Uri to String
        // Add the image URL to Firestore
        images.add(imageData)
                .addOnSuccessListener((OnSuccessListener)
                        o -> Log.d("Success image add", "Image URL added to Firestore: " + downloadUri.toString()))
                .addOnFailureListener(e -> Log.d("Fail image add", "Failed to add image URL to Firestore: " + e.getMessage()));
    }



    /**
     * Retrieves images associated with a specific itemId from the Firestore 'images' collection
     * and adds the URIs of newly created local files to the displayedUris ArrayList.
     *
     * @param itemId        The ID of the item for which images need to be retrieved.
     * @param context       The Context of the application/activity.
     * @param displayedUris The ArrayList of URIs to which the URIs of new local files will be added.
     */
    public void getImagesByItemId(String itemId, Context context,
                                  ArrayList<Uri> displayedUris, OnGetImagesCallback callback) {
        // Query the 'images' collection for documents with matching itemId
        images.whereEqualTo("itemId", itemId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    numberOfImages = queryDocumentSnapshots.size();
                    if (numberOfImages == 0) {
                        callback.onSuccessNoPicture(); // Trigger onSuccess immediately if no images found
                        return;
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String imageUrl = document.getString("imageUrl");
                        if (imageUrl != null) {
                            int startIndex = imageUrl.indexOf("images%2F")+ 9;
                            int endIndex = imageUrl.indexOf("?alt=media");
                            // Create unique file names for each image
                            String imageFileName = imageUrl.substring(startIndex, endIndex);
                            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            File imageFile = new File(storageDir, imageFileName + ".jpg");

                            // Get the URI of the newly created local file
                            Uri localUri = FileProvider.getUriForFile(context,
                                    context.getPackageName() + ".provider", imageFile);
                            displayedUris.add(localUri);
                            Log.d("URI", localUri.toString());

                            // Download image using its URL
                            downloadImageFromUrl(imageUrl, imageFile, displayedUris, callback);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Failed to query images collection: " + e.getMessage())
                );
    }

    /**
     * Downloads an image from Firebase Storage using its URL and saves it to a specified file.
     *
     * @param imageUrl  The URL of the image to be downloaded.
     * @param imageFile The File object where the downloaded image will be saved.
     */
    private void downloadImageFromUrl(String imageUrl, File imageFile,
                                      ArrayList<Uri> displayedUris, OnGetImagesCallback callback) {
        StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

        storageRef.getFile(imageFile)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image downloaded successfully
                    Log.d("Firebase Storage", "Image downloaded successfully to: " + imageFile.getAbsolutePath());
                    // Handle the downloaded image (e.g., display or further processing)
                    callback.onSuccess(displayedUris);

                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase Storage", "Failed to download image: " + e.getMessage());
                    callback.onError();
                    }
                );
    }


    public interface OnGetImagesCallback {
        void onSuccess(ArrayList<Uri> displayedUris);
        void onSuccessNoPicture();

        void onError();
    }

    /**
     * Updates an item in the Firestore database with new values
     *
     * @param old_item  The item to be replaced.
     * @param new_item  The item from which the new values should come.
     */
    public void updateItemInDB(Item old_item, Item new_item) {
        itemId = old_item.getID();
        Map<String, Object> data = generateItemHashMap(new_item);
        inventory.document(itemId).set(data);
    }

    public void updateImagesInDB(Item old_item, ArrayList<Uri> uris) {
        itemId = old_item.getID();
        deleteImagesByItemId(itemId); // delete every images of that item
        addImagesToDB(uris); // add new images to database
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
            itemId = item.getID();
            inventory.document(itemId).delete();
            deleteImagesByItemId(itemId);
            Log.d("Firestore", "Item deleted Successfully");
        }
        else{
            Log.d("Firestore", "Deletion failed, item has no ID specified");
        }
    }

    /**
     * Deletes images associated with a specific itemId from the 'images' collection and Firebase Storage.
     *
     * @param itemId The ID of the item for which images need to be deleted.
     */
    private void deleteImagesByItemId(String itemId) {
        // Query the 'images' collection for documents with matching itemId
        images.whereEqualTo("itemId", itemId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String imageUrl = document.getString("imageUrl");
                        deleteImageDocumentAndStorage(document, imageUrl);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Failed to query images collection: " + e.getMessage())
                );
    }

    /**
     * Deletes an image document from Firestore and the associated image from Firebase Storage.
     *
     * @param document The QueryDocumentSnapshot representing the image document to be deleted.
     * @param imageUrl represent the image url to be deleted.
     */
    private void deleteImageDocumentAndStorage(QueryDocumentSnapshot document, String imageUrl) {
        if (imageUrl != null) {
            // Delete the image document from Firestore
            if(document != null){
                document.getReference().delete()
                        .addOnSuccessListener(aVoid ->
                                Log.d("Firestore", "Image document deleted successfully")
                        )
                        .addOnFailureListener(e ->
                                Log.e("Firestore", "Failed to delete image document: " + e.getMessage())
                        );
            }
            // Delete the image from Firebase Storage
            StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);
            storageRef.delete()
                    .addOnSuccessListener(aVoid ->
                            Log.d("Firebase Storage", "Image deleted successfully")
                    )
                    .addOnFailureListener(e ->
                            Log.e("Firebase Storage", "Failed to delete image from Storage: " + e.getMessage())
                    );
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

    public int getNumberOfImages(){
        return this.numberOfImages;
    }
    public ArrayList<Uri> getDownloadUris(){
        return this.downloadUris;
    }
}

