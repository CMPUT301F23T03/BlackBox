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
public class InventoryAddFragment extends Fragment {
    private EditText itemName;  // itemName text box
    private EditText itemValue; // itemValue text box
    private EditText itemDescription;   // itemDescription text box

    /**
     * Default constructor for the InventoryAddFragment.
     */
    public InventoryAddFragment(){}

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
        View addItemFragmentLayout = inflater.inflate(R.layout.add_fragment, container, false);
        return addItemFragmentLayout;
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

        // Scroll view settings
        ScrollView sv = (ScrollView) view.findViewById(R.id.scrollView2);
        //sv.post(new Runnable() {
        //    public void run() {
        //        sv.smoothScrollTo(0, 270);
        //    }
        //});

        // Get text fields
        itemName = view.findViewById(R.id.name_editText);
        itemValue = view.findViewById(R.id.value_editText);
        itemDescription = view.findViewById(R.id.desc_editText);

        // Add an item by clicking the small add button
        Button small_add_button = view.findViewById(R.id.small_add_button);
        small_add_button.setOnClickListener(v -> {

            // Get text field values as String
            String name = itemName.getText().toString();
            String value = itemValue.getText().toString();
            String desc = itemDescription.getText().toString();

            // Create an Item object and send it to the inventory fragment
            InventoryFragment inventoryFragment;
            Item new_item = new Item(name, Integer.parseInt(value), desc);
            inventoryFragment = InventoryFragment.newInstance(new_item);

            // Switch to the inventory fragment
            switchFragment(inventoryFragment);
        });

        // Back button - go back to the inventory fragment
        Button backbutton = (Button) view.findViewById(R.id.back_button);
        backbutton.setOnClickListener((v) -> {
            InventoryFragment inventoryFragment = new InventoryFragment();
            switchFragment(inventoryFragment);
        });
    }

    /**
     * Switch to a new fragment by replacing the current fragment in the layout container.
     *
     * @param fragment The new fragment to replace the current one.
     */
    private void switchFragment(Fragment fragment) {
        // Create a FragmentManager from the support library
        FragmentManager fm =  getFragmentManager();
        // Create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // Replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        // Save the changes
        fragmentTransaction.commit();
    }
}