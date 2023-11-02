package com.example.blackbox;

import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;

public class InventoryFragment extends Fragment {
    ListView itemViewList;                 // ListView of all Item views
    ArrayAdapter<Item> inventoryAdapter;   // customized array adapter
    ArrayList<Item> itemList;              // list of Item objects
    Button addButton;                      // add item button
    private Context activityContext;
    int index;                             // index of an Item in the inventory list

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

    public void onItemEdited(String name, String value, String desc) {
        Item itemToEdit = itemList.get(index);
        itemToEdit.setName(name);
        itemToEdit.setEstimatedValue(Integer.parseInt(value));
        itemToEdit.setDescription(desc);
        inventoryAdapter = new InventoryListAdapter(activityContext, itemList);
        itemViewList.setAdapter(inventoryAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize a list of items
        itemViewList = (ListView) view.findViewById(R.id.item_list);
        itemList = new ArrayList<Item>();

        // check if there is any item being added
        Bundle arguments = getArguments();
        if (arguments != null) {
            Item new_item = (Item) arguments.getSerializable("item");
            if (new_item != null) {
                itemList.add(new_item);
            }
        }

        // display the inventory list
        inventoryAdapter = new InventoryListAdapter(activityContext, itemList);
        itemViewList.setAdapter(inventoryAdapter);

        // add an item - display add fragment
        addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener((v) -> {
            InventoryAddFragment myFragment = new InventoryAddFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFragment, myFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // edit item - display edit fragment
        itemViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // get item selected and send it to edit fragment to edit
                Item item = itemList.get(i);
                index = i;
                InventoryEditFragment fragment = new InventoryEditFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // switch to edit fragment
                transaction.replace(R.id.contentFragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
