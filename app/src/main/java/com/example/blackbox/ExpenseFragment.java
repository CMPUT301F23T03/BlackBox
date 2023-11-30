package com.example.blackbox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.inventory.InventoryDB;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.tag.TagDB;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.utils.NavigationManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.blackbox.profile.ProfileFragment;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

/**
 * Planned class which will display some summary info
 */
public class ExpenseFragment extends Fragment{
    private ListView expenseTagListView;

    private Context activityContext;
    private InventoryDB inventoryDB;
    private ArrayList<Item> itemList;
    private ExpenseListAdapter expenseAdapter;
    private ArrayList<Item> expenseItemList;
    private TextView totalExpenseTextView;
    private TextView info_text;
    private TagDB tagDB;
    private ArrayList<Tag> tagList;
    private ListenerRegistration tagDBListener;
    private ListenerRegistration inventoryDBListener;
    private GoogleAuthDB googleAuthDB = new GoogleAuthDB();

    public ExpenseFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).toggleBottomNavigationView(true);
        View view = inflater.inflate(R.layout.expense_fragment, container, false);

        // Display profile picture (taken from the Google account)
        ImageButton profilePicture = view.findViewById(R.id.profile_button);
        googleAuthDB.displayGoogleProfilePicture(profilePicture, 80, 80, this);

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize database
        inventoryDB = new InventoryDB();
        tagDB = new TagDB();

        tagList = new ArrayList<>();
        itemList = new ArrayList<>();
        expenseTagListView = view.findViewById(R.id.tag_expense_list);
        info_text = view.findViewById(R.id.info_text);
        expenseAdapter = new ExpenseListAdapter(activityContext, tagList);
        expenseTagListView.setAdapter(expenseAdapter);
        expenseAdapter.notifyDataSetChanged();

        // Setting up DB listeners
        tagDBListener = tagDB.getTags()
                .whereEqualTo("user_id", googleAuthDB.getUid())
                .orderBy("update_date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // An error occurred while fetching the data
                            Log.e("Firestore", "Error getting inventory", e);
                            return;
                        }
                        tagList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String name = doc.getString("name");
                            int col = doc.getLong("color").intValue();
                            String desc = doc.getString("description");
                            String colorName = doc.getString("color_name");
                            String dbID = doc.getId();
                            String userID = doc.getString("user_id");
                            Tag tag = new Tag(name, col, colorName, desc, dbID, userID);
                            tag.setDateUpdatedWithString(doc.getString("update_date"));
                            // Only add tags that belong to the current user to the list to display
                            tagList.add(tag);
                        }
                        // Notify the adapter that the data has changed
                        hideView();
                        expenseAdapter.notifyDataSetChanged();
                    }
                });

        inventoryDBListener = inventoryDB.getInventory()
                .whereEqualTo("user_id", googleAuthDB.getUid())
                // whenever database is update it is reordered by add date
                .orderBy("update_date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // An error occurred while fetching the data
                            Log.e("Firestore", "Error getting inventory", e);
                            return;
                        }
                        itemList.clear();
                        List<Task<DocumentSnapshot>> tagTasks = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            String name = doc.getString("name");
                            Double val = doc.getDouble("value");
                            String desc = doc.getString("description");
                            String make = doc.getString("make");
                            String model = doc.getString("model");
                            String serialNumber = doc.getString("serial_number");
                            String comment = doc.getString("comment");
                            String dateOfPurchase = doc.getString("purchase_date");
                            String dbID = doc.getId();
                            String userID = doc.getString("user_id");
                            ArrayList<Tag> tags = new ArrayList<>();
                            List<String> tagIDs = (List<String>) doc.get("tags");

                            Item item = new Item(name, tags, dateOfPurchase, val, make, model, serialNumber, desc, comment, dbID, userID);
                            if (tagIDs != null && !tagIDs.isEmpty()) {
                                for (String tagID : tagIDs) {
                                    Task<DocumentSnapshot> tagTask = tagDB.getTags().document(tagID).get();
                                    tagTasks.add(tagTask);
                                    Log.d("Firestore", "added task");
                                    tagTask.addOnSuccessListener(tagSnapshot -> {
                                        fetchTagForItem(item, tagSnapshot);
                                    });
                                }
                            }
                            itemList.add(item);
                        }
                        // Notify the adapter that the data has changed
                        expenseAdapter.notifyDataSetChanged();
                    }
                });

        // When profile icon is clicked, switch to profile fragment
        ImageButton profileButton = view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                NavigationManager.switchFragmentWithoutBack(profileFragment, getParentFragmentManager());
            }
        });

    }

    private void hideView() {
        if (tagList.isEmpty()) {
            info_text.setVisibility(View.VISIBLE);
            expenseAdapter.notifyDataSetChanged();
        }
    }

    private void fetchTagForItem(Item item,  DocumentSnapshot document) {
        // Access the db instance from InventoryDB
        String name = document.getString("name");
        int color = document.getLong("color").intValue();
        String colorName = document.getString("color_name");
        String description = document.getString("description");
        String dataBaseID = document.getId();
        String userID = document.getString("user_id");
        // Create a Tag object with the retrieved data
        Tag tag = new Tag(name, color, colorName, description, dataBaseID, userID);
        item.getTags().add(tag);
    }

    /**
     * Called when the fragment is attached to an activity.
     *
     * @param context The context of the activity to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * Called when the fragment is detached from the activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        activityContext = null;
        tagDBListener.remove();
        inventoryDBListener.remove();
    }
}
