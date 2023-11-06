package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * A Fragment responsible for adding a new item to the inventory. Allows users to input the item's name, value, and description.
 */
public class InventoryAddFragment extends InventoryAddEditFragment {

    /**
     * Default constructor for the InventoryAddFragment.
     */
    public InventoryAddFragment(){
        super(R.layout.add_fragment);
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

        // Add an item by clicking the small add button
        Button small_add_button = view.findViewById(R.id.small_save_button);
        small_add_button.setOnClickListener(v -> {
            if (validateInput()) {
                generateItem();
            }
        });
    }
}