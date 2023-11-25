package com.example.blackbox;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import com.example.blackbox.inventory.InventoryDB;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.tag.Tag;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class InventoryDBTest {

    private final Tag basicTag = new Tag("Name", 1, "Color", "Description");
    private final ArrayList<Tag> tag_list = new ArrayList<>();

    /**
     * Deletes all items in the inventory DB
     * @param inventoryDB this is an InventoryDB object
     */
    public static void clearInventoryDB(InventoryDB inventoryDB) {
        CollectionReference inventoryRef = inventoryDB.getInventory();
        Task<QuerySnapshot> querySnapshotInventory = inventoryRef.get();
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
     * Deletes all items in the inventory DB
     */
    public static void clearInventoryDB(){
        InventoryDB inventoryDB = new InventoryDB();
        clearInventoryDB(inventoryDB);
    }

    /**
     * Test adding an item
     */
    @Test
    public void testAddItem(){
        InventoryDB inventoryDB = new InventoryDB("test database");
        tag_list.add(basicTag);
        Item basicItem = new Item("Name",  tag_list, "2023-08-11", Double.valueOf(120), "12", "12", "12321", "Hi", "no");
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
        clearInventoryDB(inventoryDB);
    }

    /**
     * Test editing an item
     */
    @Test
    public void testEditItem(){
        InventoryDB inventoryDB = new InventoryDB("test database");
        tag_list.add(basicTag);
        Item basicItem = new Item("Name",  tag_list, "2023-08-11", Double.valueOf(120), "12", "12", "12321", "Hi", "no");
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
        clearInventoryDB(inventoryDB);
    }

    /**
     *
     */
    public static void checkItemTags(){
    }
}
