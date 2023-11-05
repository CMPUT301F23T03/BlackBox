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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * A Fragment that displays and manages an inventory of items. It includes features to add and edit items.
 */
public class InventoryFragment extends Fragment {
    ListView itemViewList;
    ArrayAdapter<Item> inventoryAdapter;
    ArrayList<Item> itemList;
    Button addButton;
    private Context activityContext;
    InventoryDB inventoryDB;
    InventoryEditFragment inventoryEditFragment = new InventoryEditFragment();
    InventoryAddFragment inventoryAddFragment = new InventoryAddFragment();

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
    }

    /**
     * Create a new instance of the InventoryFragment with the provided Item object as an argument.
     *
     * @param item The Item object to be associated with the fragment.
     * @return A new instance of InventoryFragment.
     */
    static InventoryFragment newInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable("item",item);    // serialize Item object
        InventoryFragment fragment = new InventoryFragment();
        fragment.setArguments(args);    // set the Item object to be this fragment's argument
        return fragment;
    }

    /**
     * Create a new instance of the InventoryFragment with the provided Item object and an integer as arguments.
     *
     * @param item The Item object to be associated with the fragment.
     * @param index The index of Item object to be associated with the fragment.
     * @return A new instance of InventoryFragment.
     */
    static InventoryFragment newInstance(Item item, int index) {
        Bundle args = new Bundle();
        args.putSerializable("edited item",item);    // serialize Item object
        args.putSerializable("index",index);
        InventoryFragment fragment = new InventoryFragment();
        fragment.setArguments(args);    // set the Item object to be this fragment's argument
        return fragment;
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

        // listener for data changes in DB
        inventoryDB.getInventory().addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    itemList.add(new Item(name, val, desc));
                }
                // Notify the adapter that the data has changed
                inventoryAdapter.notifyDataSetChanged();
            }
        });

        // check if there is any item to add or edit
        onItemAdded();
        onItemEdited();

        // add an item - display add fragment
        addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener((v) -> {
            loadFragment(inventoryAddFragment);
        });

        // edit item - display edit fragment
        itemViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                inventoryEditFragment = InventoryEditFragment.newInstance(i);
                loadFragment(inventoryEditFragment);
            }
        });
    }

    /**
     * Switch to a new fragment by replacing the current fragment in the layout container.
     *
     * @param fragment The new fragment to replace the current one.
     */
    private void loadFragment(Fragment fragment) {
        // create a FragmentManager from the support library
        FragmentManager fm =  getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        // save the changes
        fragmentTransaction.commit();
    }

    /**
     * Edits an item in the Firestore database with the updated data provided in the 'editedItem' object.
     *
     * @param snapshots   A list of Firestore DocumentSnapshots representing items in the database.
     * @param editedItem  The updated item object containing the new data for the item.
     * @param index       The index of the item to be edited within the 'snapshots' list.
     */
    private void editItemInDB(ArrayList<DocumentSnapshot> snapshots, Item editedItem, int index) {
        String itemID = snapshots.get(index).getId();
        inventoryDB.updateItemInDB(itemID, editedItem);
    }

    /**
     * Check if an item has been added and, if so, add it to the database.
     */
    public void onItemAdded() {
        // check if there is any item being added
        Bundle arguments = getArguments();
        if (arguments != null) {
            Item newItem = (Item) arguments.getSerializable("item");
            if (newItem != null) {
                inventoryDB.addItemToDB(newItem);
            }
        }
    }

    /**
     *  Check if an item has been edited and, if so, edit it in the database.
     */
    public void onItemEdited() {
        // check if there is any item being edited
        Bundle arguments = getArguments();
        if (arguments != null) {
            Item editedItem = (Item) arguments.getSerializable("edited item");
            if (editedItem != null) {
                ArrayList<DocumentSnapshot> snapshots = new ArrayList<>();
                int index = (int) arguments.getSerializable("index");
                inventoryDB.getInventory()
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        snapshots.add(document);
                                    }
                                    editItemInDB(snapshots, editedItem, index);
                                } else {
                                    Log.d("ERROR", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }
    }
}
