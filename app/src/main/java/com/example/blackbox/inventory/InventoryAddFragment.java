package com.example.blackbox.inventory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.blackbox.R;

/**
 * A Fragment responsible for adding a new item to the inventory. Allows users to input the item's name, value, description, tags, and more.
 */
public class InventoryAddFragment extends InventoryAddEditFragment {

    /**
     * Default constructor for the InventoryAddFragment.
     */
    public InventoryAddFragment(){
        super(R.layout.add_fragment);
    }

    /**
     * Create a new instance of the InventoryAddFragment with the provided Item object as an argument.
     *
     * @param item The Item object to be associated with the fragment.
     * @return A new instance of InventoryEditFragment.
     */
    public static InventoryAddFragment newInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable("item", item);    // serialize Item object
        InventoryAddFragment fragment = new InventoryAddFragment();
        fragment.setArguments(args);    // set the Item object to be this fragment's argument
        return fragment;
    }

    /**
     * Called when the fragment's view has been created. Handles user interactions for adding a new item.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup the fragment's db and text fields
        setupFragment(view);
        // get the index of item to be edited
        Bundle arguments = getArguments();
        if (arguments != null) {
            // Arguments were provided when creating the fragment
            if (arguments.containsKey("item")) {
                // "item" argument was provided
                Item item = (Item) arguments.getSerializable("item");
                // Now you can use the "item" object
                adjustFields(item);
                Toast.makeText(requireContext(), "Serial Number Added", Toast.LENGTH_SHORT).show();
            }
        }



        // Add an item by clicking the small add button
        Button small_add_button = view.findViewById(R.id.small_save_button);
        small_add_button.setOnClickListener(v -> {
            if (validateInput()) {
                add();
            }
        });
    }
}