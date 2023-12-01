package com.example.blackbox;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemImageDB {
    private CollectionReference images;
    private FirebaseFirestore db;

    public ItemImageDB(){
        db = FirebaseFirestore.getInstance();
        images =db.collection("images");
    }

    public CollectionReference getImages(){
        return images;
    }

    public void addImageToDB(String itemID, Uri imageUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("itemID", itemID);
        data.put("imageUrl", imageUrl.toString());
        images.add(data);
    }

    public void getImagesFromItem(String itemID, OnGetImagesCallback callBack){
        images.whereEqualTo("itemID", itemID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(task.getResult());
                    } else {
                        Log.e("ImageDB", "Error getting images: " + task.getException().getMessage());
                        callBack.onError(task.getException().getMessage());
                    }
                });
    }

    public interface OnGetImagesCallback {
        void onSuccess(QuerySnapshot imageSnapshots);

        void onError(String errorMessage);

        void onSuccess(ArrayList<Uri> displayedUris);

        void onError();
    }
}
