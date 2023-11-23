package com.example.blackbox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * User MUST already be logged in for all these tests to pass
 */
public class ProfileDBTest {
    private GoogleAuthDB googleAuthDB = new GoogleAuthDB();

    /**
     * Deletes all items in the test profile DB
     * @param profileDB this is an ProfileDB object
     */
    public static void clearProfileDB(ProfileDB profileDB) {
        CollectionReference profileRef = profileDB.getProfileRef();
        Task<QuerySnapshot> querySnapshotInventory = profileRef.get();
        Log.d("Firestore", "Before listener");

        // Create a CountDownLatch with an initial count of 1
        CountDownLatch latch = new CountDownLatch(1);

        querySnapshotInventory.addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d("Firestore", "Success Achieved");
            List<Task<Void>> deletedItems = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Delete each document in the collection
                deletedItems.add(profileRef.document(documentSnapshot.getId()).delete());
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
     * Deletes all items in the main profile DB
     */
    public static void clearProfileDB(){
        ProfileDB profileDB = new ProfileDB();
        clearProfileDB(profileDB);
    }

    /**
     * Test editing profile's name and bio
     */
    @Test
    public void testEditProfile(){
        ProfileDB profileDB = new ProfileDB("profile_test");
        Profile newProfile = new Profile(googleAuthDB.getUid(), "Micheal Buro", "I love OpenGL", googleAuthDB.getEmail());
        profileDB.addEditProfile(newProfile);
        CollectionReference profileRef = profileDB.getProfileRef();
        Query query = profileRef.whereEqualTo("name", newProfile.getName());
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
                Profile basicProfile = new Profile(googleAuthDB.getUid(), "Name2", "I love OpenGL", googleAuthDB.getEmail());
                profileDB.addEditProfile(basicProfile);
                Query query2 = profileRef.whereEqualTo("name", newProfile.getName());
                Task<QuerySnapshot> task2 = query2.get();
                QuerySnapshot querySnapshot2;
                try {
                    querySnapshot2 = Tasks.await(task2);
                    assertTrue(querySnapshot2.isEmpty());
                }
                catch (Exception e){
                    fail("Firestore Query Failed");
                }
                Query query3 = profileRef.whereEqualTo("name", basicProfile.getName());
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
        clearProfileDB(profileDB);
    }
}
