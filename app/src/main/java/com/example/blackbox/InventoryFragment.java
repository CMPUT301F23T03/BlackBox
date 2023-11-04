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

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InventoryFragment extends Fragment {
    ListView itemViewList;                 // ListView of all Item views
    ArrayAdapter<Item> inventoryAdapter;   // customized array adapter
    ArrayList<Item> itemList;              // list of Item objects
    Button addButton;                      // add item button
    private Context activityContext;       // context of MainActivity
    int index;                             // index of an Item in the inventory list
    InventoryDB inventoryDB;               // database connector

    InventoryEditFragment inventoryEditFragment = new InventoryEditFragment();

    InventoryAddFragment inventoryAddFragment = new InventoryAddFragment();


    public InventoryFragment(){}
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityContext = context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activityContext = null;
    }
    static InventoryFragment newInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable("item",item);    // serialize Item object
        InventoryFragment fragment = new InventoryFragment();
        fragment.setArguments(args);    // set the Item object to be this fragment's argument
        return fragment;
    }
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View ItemFragmentLayout = inflater.inflate(R.layout.inventory_fragment, container, false);
        return ItemFragmentLayout;
    }

    public void onItemAdded() {
        // check if there is any item being added
        Bundle arguments = getArguments();
        if (arguments != null) {
            Item new_item = (Item) arguments.getSerializable("item");
            if (new_item != null) {
                inventoryDB.addItemToDB(new_item);
            }
        }
    }

    public void onItemEdited(String name, String value, String desc) {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialize database
        inventoryDB = new InventoryDB();

        // check if there is any item to add
        onItemAdded();

        // display the inventory list
        itemList = new ArrayList<>();
        itemViewList = (ListView) view.findViewById(R.id.item_list);
        inventoryAdapter = new InventoryListAdapter(activityContext, itemList);
        itemViewList.setAdapter(inventoryAdapter);

        // add an item - display add fragment
        addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener((v) -> {
            loadFragment(inventoryAddFragment);
        });

        // edit item - display edit fragment
        itemViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                loadFragment(inventoryEditFragment);
            }
        });
    }

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
}
