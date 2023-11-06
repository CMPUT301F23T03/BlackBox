package com.example.blackbox;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreTest {
    public static void clearTagDB(){
        TagDB tags = new TagDB();
        CollectionReference tagRef = tags.getTags();
        Task<QuerySnapshot> querySnapshotTask = tagRef.get();
        querySnapshotTask.addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Delete each document in the collection
                tagRef.document(documentSnapshot.getId()).delete();
            }
        });
    }
}
