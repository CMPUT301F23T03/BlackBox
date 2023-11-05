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
 * A Fragment responsible for editing an inventory item's details.
 */
public class InventoryEditFragment extends Fragment {
    private EditText itemName;  // itemName text box
    private EditText itemValue; // itemValue text box
    private EditText itemDescription;   // itemDescription text box

    /**
     * Default constructor for the InventoryEditFragment.
     */
    public InventoryEditFragment(){}

    /**
     * Create a new instance of the InventoryEditFragment with the provided Item object as an argument.
     *
     * @param index The index of Item object to be associated with the fragment.
     * @return A new instance of InventoryEditFragment.
     */
    static InventoryEditFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putSerializable("itemToEditIndex", index);    // serialize Item object
        InventoryEditFragment fragment = new InventoryEditFragment();
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
        View editItemFragmentLayout = inflater.inflate(R.layout.edit_fragment, container, false);
        return editItemFragmentLayout;
    }

    /**
     * Called when the fragment's view has been created. Handles user interactions for editing an item.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the index of item to be edited
        int itemToEditIndex = (int) getArguments().getSerializable("itemToEditIndex");

        // get text fields
        itemName = view.findViewById(R.id.name_editText);
        itemValue = view.findViewById(R.id.value_editText);
        itemDescription = view.findViewById(R.id.desc_editText);

        // save an edited item by clicking the small add button
        Button small_save_button = view.findViewById(R.id.small_save_button);
        small_save_button.setOnClickListener(v -> {

            // get text field values as String
            String name = itemName.getText().toString();
            String value = itemValue.getText().toString();
            String desc = itemDescription.getText().toString();
            Item editedItem = new Item(name, Double.parseDouble(value), desc);

            // pass in edited values to inventory fragment
            InventoryFragment inventoryFragment;
            inventoryFragment = InventoryFragment.newInstance(editedItem, itemToEditIndex);

            // switch to inventory fragment
            switchFragment(inventoryFragment);
        });

        // back button - go back to inventory fragment
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
