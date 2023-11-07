package com.example.blackbox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A Fragment that displays and manages an inventory of items. It includes features to add and edit items.
 */
public class InventoryFragment extends Fragment {
    ListView itemViewList;
    ArrayAdapter<Item> inventoryAdapter;
    ArrayList<Item> itemList;
    Button addButton;
    ListenerRegistration dbListener;
    private Context activityContext;
    InventoryDB inventoryDB;
    InventoryEditFragment inventoryEditFragment = new InventoryEditFragment();
    InventoryAddFragment inventoryAddFragment = new InventoryAddFragment();
    private TextView totalSumTextView;
    // Add a member variable to store the total sum
    private double totalSum = 0.0;

    /**
     * Default constructor for the InventoryFragment.
     */
    public InventoryFragment(){}

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
        dbListener.remove();
    }


    /**
     * Called to create the view for the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     * @return The view for the fragment.
     */
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View ItemFragmentLayout = inflater.inflate(R.layout.inventory_fragment, container, false);
        return ItemFragmentLayout;
    }

    /**
     * Called when the fragment's view has been created.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize database
        inventoryDB = new InventoryDB();

        // display the inventory list
        itemList = new ArrayList<>();
        itemViewList = (ListView) view.findViewById(R.id.item_list);
        inventoryAdapter = new InventoryListAdapter(activityContext, itemList);
        itemViewList.setAdapter(inventoryAdapter);
        totalSumTextView = view.findViewById(R.id.total_sum);


        // listener for data changes in DB
        dbListener =
                inventoryDB.getInventory()
                // whenever database is update it is reordered by add date
                // THIS MAY BREAK THINGS ONCE SORTING IS IMPLEMENTED
                .orderBy("update_date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle any errors or exceptions
                    return;
                }
                itemList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    String name = doc.getString("name");
                    Double val = doc.getDouble("value");
                    String desc = doc.getString("description");
                    String make = doc.getString("make");
                    String model = doc.getString("model");
                    String serialNumber = doc.getString("serial_number");
                    String comment = doc.getString("comment");
                    String dbID = doc.getId();
                    ArrayList<Tag> tags = new ArrayList<>();
                    String dateOfPurchase = "";

                    List<String> tagIDs = (List<String>) doc.get("tags");

                    Item item = new Item(name, tags, dateOfPurchase, val, make, model, serialNumber, desc, comment, dbID);
                    if (tagIDs != null && !tagIDs.isEmpty()) {
                        fetchTagsForItem(item, tagIDs);
                    } else {
                        // Add the item to the list without tags
                        itemList.add(item);
                    }
                }
                // Notify the adapter that the data has changed
                inventoryAdapter.notifyDataSetChanged();
                updateTotalSum();
                Log.d("Firestore", activityContext.toString());
                Log.d("Firestore", "Processed Update");
            }
        });

        // add an item - display add fragment
        addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener((v) -> {
            NavigationManager.switchFragment(inventoryAddFragment, getParentFragmentManager());
        });

        // edit item - display edit fragment
        itemViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                inventoryEditFragment = InventoryEditFragment.newInstance(itemList.get(i));
                NavigationManager.switchFragment(inventoryEditFragment, getParentFragmentManager());
            }
        });

    }

    /**
     * Fetches tags associated with an item from the Firestore database and
     * populates the item's tag list.
     *
     * @param item
     * @param tagIDs
     */
    private void fetchTagsForItem(Item item, List<String> tagIDs) {
        // Access the db instance from InventoryDB
        FirebaseFirestore db = inventoryDB.getDb();

        for (String tagID : tagIDs) {
            db.collection("tags").document(tagID).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String name = document.getString("name");
                                    int color = document.getLong("color").intValue();
                                    String colorName = document.getString("colorName");
                                    String description = document.getString("description");
                                    // Create a Tag object with the retrieved data

                                    Tag tag = new Tag(name, color, colorName, description);
                                    item.getTags().add(tag);

                                    // Check if all tags have been retrieved
                                    if (item.getTags().size() == tagIDs.size()) {
                                        // Add the item to the list
                                        itemList.add(item);
                                        // Notify the adapter that the data has changed
                                        inventoryAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    // Handle the case where the document does not exist
                                }
                            } else {
                                // Handle errors or exceptions
                            }
                        }
                    });
        }
    }

    private double calculateTotalSum(ArrayList<Item> items) {
        double totalSum = 0.0;
        for (Item item : items) {
            totalSum += item.getEstimatedValue();
        }
        return totalSum;
    }

    public void updateTotalSum() {
        double totalSum = calculateTotalSum(itemList);
        totalSumTextView.setText(String.format("%.2f",totalSum));
    }
}
