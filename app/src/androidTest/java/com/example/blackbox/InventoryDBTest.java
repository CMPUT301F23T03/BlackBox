package com.example.blackbox;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class InventoryDBTest {
    /**
     * This method synchronously deletes all items from a specified collection
     * @param items
     *      The database manager to delete with
     */
    public static void clearInventoryDB(InventoryDB items) {
        CollectionReference invRef = items.getInventory();
        Task<QuerySnapshot> querySnapshotTask = invRef.get();
        Log.d("Firestore", "Before listener");

        // Create a CountDownLatch with an initial count of 1
        CountDownLatch latch = new CountDownLatch(1);

        querySnapshotTask.addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d("Firestore", "Success Achieved");
            List<Task<Void>> deleteTasks = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Delete each document in the collection
                deleteTasks.add(invRef.document(documentSnapshot.getId()).delete());
            }

            // Wait for all delete tasks to complete
            Tasks.whenAll(deleteTasks)
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
     * Clear the default inventoryDB
     */
    public static void clearInventoryDB(){
        InventoryDB items = new InventoryDB();
        clearInventoryDB(items);
    }

    /**
     *
     */
    public static void checkItemTags(){
    }
}
