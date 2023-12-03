package com.example.blackbox;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import android.content.Context;

import android.net.Uri;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.inventory.InventoryDB;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.tag.Tag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class InventoryDBTest {
    private final Tag basicTag = new Tag("Name", 1, "Color", "Description");
    private final ArrayList<Tag> tag_list = new ArrayList<>();
    private InventoryDB inventoryDB;
    private FirebaseStorage firebaseStorage;
    private StorageReference imagesStorageReference;
    private ArrayList<Uri> downloadUris;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        firebaseStorage = FirebaseStorage.getInstance();
        imagesStorageReference = firebaseStorage.getReference().child("images");
        inventoryDB = new InventoryDB(); // Initialize your InventoryDB instance
    }


    /**
     * Deletes all items in the inventory DB
     * @param inventoryDB this is an InventoryDB object
     */
    public static void clearInventoryDB(InventoryDB inventoryDB, String userID) {
        CollectionReference inventoryRef = inventoryDB.getInventory();
        Task<QuerySnapshot> querySnapshotInventory = inventoryRef.whereEqualTo("user_id", userID).get();
        Log.d("Firestore", "Before listener");

        // Create a CountDownLatch with an initial count of 1
        CountDownLatch latch = new CountDownLatch(1);

        querySnapshotInventory.addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d("Firestore", "Success Achieved");
            List<Task<Void>> deletedItems = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Delete each document in the collection
                deletedItems.add(inventoryRef.document(documentSnapshot.getId()).delete());
            }

            // Wait for all delete tasks to complete
            Tasks.whenAll(deletedItems)
                    .addOnCompleteListener(task -> {
                        // Release the latch when all tasks are done
                        latch.countDown();
                    });
        });
        try {
            // Wait for the latch to be counted down to 0
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all items in the inventory DB of specified user
     */
    public static void clearInventoryDB(String userID){
        InventoryDB inventoryDB = new InventoryDB();
        clearInventoryDB(inventoryDB, userID);
    }

    /**
     * Deletes all items in the inventory DB of current user
     */
    public static void clearInventoryDB(){
        InventoryDB inventoryDB = new InventoryDB();
        String userID = new GoogleAuthDB().getUid();
        clearInventoryDB(inventoryDB, userID);
    }


    /**
     * Test adding an item
     */
    @Test
    public void testAddItem(){
        String userID = new GoogleAuthDB().getUid();
        basicTag.setUserID(userID);
        InventoryDB inventoryDB = new InventoryDB("test database");
        tag_list.add(basicTag);

        Item basicItem = new Item("Name",  tag_list, "2023-08-11", Double.valueOf(120), "12", "12", "12321", "Hi", "no");
        basicItem.setUserID(userID);
        inventoryDB.addItemToDB(basicItem);
        CollectionReference inventory = inventoryDB.getInventory();
        Query query = inventory.whereEqualTo("name", basicItem.getName());
        Task<QuerySnapshot> task = query.get();
        try {
            QuerySnapshot querySnapshot = Tasks.await(task);
            assertFalse(querySnapshot.isEmpty());
        } catch (Exception e) {
            Log.d("Firestore", "Exception Encountered");
        }
        clearInventoryDB(inventoryDB, userID);
    }

    /**
     * Test editing an item
     */
    @Test
    public void testEditItem(){
        String userID = new GoogleAuthDB().getUid();
        basicTag.setUserID(userID);

        InventoryDB inventoryDB = new InventoryDB("test database");
        tag_list.add(basicTag);
        Item basicItem = new Item("Name",  tag_list, "2023-08-11", Double.valueOf(120), "12", "12", "12321", "Hi", "no");
        basicItem.setUserID(userID);
        inventoryDB.addItemToDB(basicItem);
        CollectionReference inventory = inventoryDB.getInventory();
        Query query = inventory.whereEqualTo("name", basicItem.getName());
        Task<QuerySnapshot> task = query.get();
        QuerySnapshot querySnapshot;
        try {
            querySnapshot = Tasks.await(task);
            assertFalse(querySnapshot.isEmpty());
            String itemID = null;
            for (QueryDocumentSnapshot doc : querySnapshot) {
                itemID = doc.getId();
            }
            if (itemID != null) {
                basicItem.setID(itemID);
                Item newItem = new Item("Name2",  tag_list, "2023-08-11", Double.valueOf(120), "12", "12", "12321", "Hi", "no");
                inventoryDB.updateItemInDB(basicItem, newItem);
                Query query2 = inventory.whereEqualTo("name", basicItem.getName());
                Task<QuerySnapshot> task2 = query2.get();
                QuerySnapshot querySnapshot2;
                try {
                    querySnapshot2 = Tasks.await(task2);
                    assertTrue(querySnapshot2.isEmpty());
                }
                catch (Exception e){
                    fail("Firestore Query Failed");
                }
                Query query3 = inventory.whereEqualTo("name", newItem.getName());
                Task<QuerySnapshot> task3 = query3.get();
                QuerySnapshot querySnapshot3;
                try {
                    querySnapshot3 = Tasks.await(task3);
                    assertFalse(querySnapshot3.isEmpty());
                }
                catch (Exception e){
                    fail("Firestore Query Failed");
                }
            }
            else{
                fail("No Item received from DB");
            }
        } catch (Exception e) {
            fail("Firestore Query Failed");
        }
        clearInventoryDB(inventoryDB, userID);
    }

    /**
     * This method tests the addition of images to the database by fetching images from the app's assets,
     * adding them to the database, and checking if the images were successfully added.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Test
    public void testAddImagesToDB() throws InterruptedException {
        GoogleAuthDB googleAuthDB = new GoogleAuthDB();
        String userID = googleAuthDB.getUid();
        ArrayList<Uri> imageList = new ArrayList<>();
        try {
            // Retrieve images from the app's assets
            Log.d("Test file name", "Getting file names");
            List<String> imageFiles = getAssetImageFiles();
            for (String fileName : imageFiles) {
                // Construct URI for each image file in assets
                String filePath = context.getFilesDir() + File.separator + fileName;
                copyImageFromAssets(fileName, filePath);
                Uri imageUri = Uri.fromFile(new File(filePath));
                imageList.add(imageUri);
            }
        } catch (IOException e) {
            Log.d("Test file name", "Fail to get file names");
            e.printStackTrace();
        }

        CountDownLatch latch = new CountDownLatch(1);
        inventoryDB.addImagesToDB(imageList);
        downloadUris= inventoryDB.getDownloadUris();
        latch.countDown();
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed);
    }


    /**
     * Clean up by deleting the copied image files after testing is done.
     */
    @After
    public void cleanup() {
        // Delete the copied image files when the test is done
        String[] files = {};
        try {
            files = context.getAssets().list("imageAttach");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String file : files) {
            String filePath = context.getFilesDir() + File.separator + file;
            File imageFile = new File(filePath);
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    Log.d("Test Cleanup", "Deleted file: " + filePath);
                } else {
                    Log.d("Test Cleanup", "Failed to delete file: " + filePath);
                }
            }
        }
    }
    // Helper method to fetch image files from assets directory
    private List<String> getAssetImageFiles() throws IOException {
        List<String> imageFiles = new ArrayList<>();
        String[] files = context.getAssets().list("imageAttach");
        if (files != null) {
            for (String file : files) {
                Log.d("Test file name", file);
                imageFiles.add(file);
            }
        }
        return imageFiles;
    }

    private void copyImageFromAssets(String fileName, String destFilePath) {
        try {
            InputStream in = context.getAssets().open("imageAttach"+ "/" + fileName);
            OutputStream out = new FileOutputStream(destFilePath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
