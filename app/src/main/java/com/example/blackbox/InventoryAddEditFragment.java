package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class InventoryAddEditFragment extends Fragment {
    private EditText itemName;
    private EditText itemValue;
    private EditText itemDescription;
    private String name;
    private Double val;
    private String desc;
    private InventoryDB itemDB;
    private int fragment_id;

    /**
     * Default constructor for the InventoryAddEditFragment
     */
    public InventoryAddEditFragment(int fragment_id){
        this.fragment_id = fragment_id;
    }

    /**
     * Called to create the view for the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     * @return The view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View fragmentLayout = inflater.inflate(fragment_id, container, false);
        return fragmentLayout;
    }

    /**
     * A method which sets up the database, listeners,
     * and variables for the fragment
     * @param view
     *      The view from which to find UI elements
     */
    public void setupFragment(View view) {
        // get database
        itemDB = new InventoryDB();

        // get text fields
        itemName = view.findViewById(R.id.name_editText);
        itemValue = view.findViewById(R.id.value_editText);
        itemDescription = view.findViewById(R.id.desc_editText);
        setupBackButtonListener(view);


    }

    /**
     * A method which sets up a listener to track whether the back button is pressed
     * @param view
     *      The view from which to find UI elements
     */
    public void setupBackButtonListener(View view){
        final Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            InventoryFragment inventoryFragment = new InventoryFragment();
            NavigationManager.switchFragment(inventoryFragment, getParentFragmentManager());
        });
    }

    /**
     * A method which gets user input from the fragment and validates if it is correct
     * @return
     *      A Boolean representing whether the current user input is valid
     */
    public Boolean validateInput(){
        name = itemName.getText().toString();
        val = null;
        if (itemValue.getText().toString().length() > 0){
            val = Double.parseDouble(itemValue.getText().toString());
        }
        desc = itemDescription.getText().toString();
        if (name.length() > 0 && val != null && desc.length() > 0) {
            // allow save action
            return Boolean.TRUE;
        }
        else {
            // display error message
            Toast.makeText(getActivity(), "Missing Information", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
    }
    /**
     * A method which creates a new item and adds it to the database
     */
    public void generateItem(){
        Item new_item = new Item(name, val, desc);
        itemDB.addItemToDB(new_item);
        NavigationManager.switchFragment(new InventoryFragment(), getParentFragmentManager());
    }
}
