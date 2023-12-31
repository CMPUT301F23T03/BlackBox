package com.example.blackbox.DBTests;

import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.tag.TagDB;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TagDBTest {
    private Tag basicTag = new Tag("Name", 1, "Color", "Description");

    /**
     * This method synchronously deletes all tags from a specified collection
     * @param tags
     *      The database manager to delete with
     */
    public static void clearTagDB(TagDB tags, String userID) {
        CollectionReference tagRef = tags.getTags();
        Task<QuerySnapshot> querySnapshotTask = tagRef.whereEqualTo("user_id", userID).get();
        Log.d("Firestore", "Before listener");

        // Create a CountDownLatch with an initial count of 1
        CountDownLatch latch = new CountDownLatch(1);

        querySnapshotTask.addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d("Firestore", "Success Achieved");
            List<Task<Void>> deleteTasks = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Delete each document in the collection
                deleteTasks.add(tagRef.document(documentSnapshot.getId()).delete());
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

    public static void clearTagDB(String userID){
        TagDB tags = new TagDB();
        clearTagDB(tags, userID);
    }

    public static void clearTagDB(){
        TagDB tags = new TagDB();
        String userID = new GoogleAuthDB().getUid();
        clearTagDB(tags, userID);
    }
    @Test
    public void testAdd(){
        String userID = new GoogleAuthDB().getUid();
        TagDB tagDB = new TagDB("test collection");
        basicTag.setUserID(userID);
        tagDB.addTagToDB(basicTag);
        CollectionReference tags = tagDB.getTags();
        Query query = tags.whereEqualTo("name", basicTag.getName());
        Task<QuerySnapshot> task = query.get();
        try {
            QuerySnapshot querySnapshot = Tasks.await(task);
            assertFalse(querySnapshot.isEmpty());
        } catch (Exception e) {
            Log.d("Firestore", "Exception Encountered");
        }
        clearTagDB(tagDB, userID);
    }

    @Test
    public void testEdit(){
        String userID = new GoogleAuthDB().getUid();
        TagDB tagDB = new TagDB("test collection");
        basicTag.setUserID(userID);
        tagDB.addTagToDB(basicTag);
        CollectionReference tags = tagDB.getTags();
        Query query = tags.whereEqualTo("name", basicTag.getName());
        Task<QuerySnapshot> task = query.get();
        QuerySnapshot querySnapshot;
        try {
            querySnapshot = Tasks.await(task);
            assertFalse(querySnapshot.isEmpty());
            String tagID = null;
            for (QueryDocumentSnapshot doc : querySnapshot) {
                tagID = doc.getId();
            }
            if (tagID != null) {
                basicTag.setID(tagID);
                Tag newTag = new Tag("New Name", 2, "NewColor", "New Description");
                tagDB.editTag(basicTag, newTag);
                Query query2 = tags.whereEqualTo("name", basicTag.getName());
                Task<QuerySnapshot> task2 = query2.get();
                QuerySnapshot querySnapshot2;
                try {
                    querySnapshot2 = Tasks.await(task2);
                    assertTrue(querySnapshot2.isEmpty());
                }
                catch (Exception e){
                    fail("Firestore Query Failed");
                }
                Query query3 = tags.whereEqualTo("name", newTag.getName());
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
                fail("No Tag received from DB");
                }
        } catch (Exception e) {
            fail("Firestore Query Failed");
        }
        clearTagDB(tagDB, userID);
    }
}
