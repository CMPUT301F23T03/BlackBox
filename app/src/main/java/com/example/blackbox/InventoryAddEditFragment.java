package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public abstract class InventoryAddEditFragment extends AddEditFragment {
    private EditText itemName;
    private EditText itemValue;
    private EditText itemDescription;
    private EditText itemMake;
    private EditText itemModel;
    private EditText itemSerialNumber;
    private EditText itemComment;
    private String name;
    private Double val;
    private String desc;
    private InventoryDB itemDB;
    private String make;
    private String model;
    private String serialNumber;
    private String comment;

    /**
     * Default constructor for the InventoryAddEditFragment
     */
    public InventoryAddEditFragment(int fragment_id){
        super(fragment_id);
    }


    /**
     * A method which sets up the database, listeners,
     * and variables for the fragment
     * @param view
     *      The view from which to find UI elements
     */
    @Override
    public void setupFragment(View view) {
        // get database
        itemDB = new InventoryDB();

        // get text fields
        itemName = view.findViewById(R.id.name_editText);
        itemValue = view.findViewById(R.id.value_editText);
        itemDescription = view.findViewById(R.id.desc_editText);
        itemMake = view.findViewById(R.id.make_editText);
        itemModel = view.findViewById(R.id.model_editText);
        itemComment = view.findViewById(R.id.comment_editText);
        itemSerialNumber = view.findViewById(R.id.serial_number_editText);;
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
        make = itemMake.getText().toString();
        model = itemModel.getText().toString();
        serialNumber = itemSerialNumber.getText().toString();
        comment = itemComment.getText().toString();
        if (name.length() == 0){
            Toast.makeText(getActivity(), "Name Required", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
        else if (val == null) {
            Toast.makeText(getActivity(), "Estimated Value Required", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
        else {
            // allow input
            return Boolean.TRUE;
        }
    }
    /**
     * A method which creates a new item and adds it to the database
     */
    @Override
    public void add(){
        Item new_item = new Item(name, new ArrayList<>(), "", val, make, model, serialNumber, desc, comment);
        itemDB.addItemToDB(new_item);
        NavigationManager.switchFragment(new InventoryFragment(), getParentFragmentManager());
    }

    /**
     * A method to replace an item in the database with a new one
     * @param item
     *      The item to be replaced
     */
    public void editItem(Item item){
        Item new_item = new Item(name, new ArrayList<>(), "", val, make, model, serialNumber, desc, comment);
        itemDB.updateItemInDB(item, new_item);
        NavigationManager.switchFragment(new InventoryFragment(), getParentFragmentManager());
    }

    /**
     * A method to adjust the fields to reflect the data from an item
     * @param item
     *           The item whose info will be used to fill fields
     */
    public void adjustFields(Item item){
        itemName.setText(item.getName());
        itemDescription.setText(item.getDescription());
        itemComment.setText(item.getComment());
        itemMake.setText(item.getMake());
        itemValue.setText(item.getStringEstimatedValue());
        itemModel.setText(item.getModel());
        itemSerialNumber.setText(item.getSerialNumber());
    }
}
