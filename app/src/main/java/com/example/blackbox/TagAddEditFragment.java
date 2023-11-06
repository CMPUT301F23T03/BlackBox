package com.example.blackbox;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class TagAddEditFragment extends Fragment {
    private EditText tagName;  // itemName text box
    private EditText tagDescription;   // itemDescription text box
    private TagDB tagDB;
    private Integer selectedColor;     // the value of the currently selected color
    private String name;
    private String desc;
    private String colorName;
    private Spinner spinner;
    private ColorSpinnerAdapter spinnerAdapter;
    private int fragment_id;

    /**
     * Default constructor for the TagAddEditFragment.
     */
    public TagAddEditFragment(int fragment_id){
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
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View fragmentLayout = inflater.inflate(fragment_id, container, false);
        return fragmentLayout;
    }

    /**
     * A method which sets up the database, listeners,
     * and variables for the fragment
     * @param view
     *      The view from which to find UI elements
     */
    public void setupFragment(View view){
        // init DB
        tagDB = new TagDB();
        // init name and description boxes
        tagName = view.findViewById(R.id.name_editText);
        tagDescription = view.findViewById(R.id.desc_editText);
        // init spinner for selected color
        setupBackButtonListener(view);
        setupColorSpinnerListener(view);
    }

    /**
     * A method which sets up a listener track whether the back button is pressed
     * @param view
     *      The view from which to find UI elements
     */
    public void setupBackButtonListener(View view){
        final Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            NavigationManager.switchFragment(new TagFragment(), getParentFragmentManager());
        });
    }

    /**
     * A method which sets up a listener to keep track of the selected color
     * @param view
     *      The view from which to find UI elements
     */
    public void setupColorSpinnerListener(View view){
        // populate spinner
        spinner = view.findViewById(R.id.color_spinner);
        spinnerAdapter = new ColorSpinnerAdapter(getActivity(), new ArrayList<TagColor>());
        spinnerAdapter.populateColors(getResources());
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item here
                selectedColor = spinnerAdapter.getItem(position).getColor();
                colorName = spinnerAdapter.getItem(position).getName();
                Log.d("onItemSelected", "Selected: " + selectedColor.toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });
    }

    /**
     * A method which gets user input from the fragment and validates if it is correct
     * @return
     *      A Boolean representing whether the current user input is valid
     */
    public Boolean validateInput(){
        name = tagName.getText().toString();
        desc = tagDescription.getText().toString();
        if (name.length() > 0 && selectedColor != null && desc.length() > 0) {
            // allow save action
            return Boolean.TRUE;
            }
        else{
            // display error message
            Toast.makeText(getActivity(), "Missing Information", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
    }

    /**
     * A method which creates a new tag and adds it to the database
     */
    public void generateTag(){
        Tag new_tag = new Tag(name, selectedColor, colorName, desc);
        tagDB.addTagToDB(new_tag);
        NavigationManager.switchFragment(new TagFragment(), getParentFragmentManager());
    }

    /**
     * A method to adjust the fields to reflect the data from a tag
     */
    public void adjustFields(Tag tag){
        tagName.setText(tag.getName());
        tagDescription.setText(tag.getDescription());
        int index = spinnerAdapter.getColorIndex(tag.getColorName());
        if (index != -1){
            spinner.setSelection(index);
        }
    }

    /**
     * A method to replace a tag in the database with a new one
     * @param tag
     *      The tag to be replaced
     */
    public void editTag(Tag tag){
        Tag new_tag = new Tag(name, selectedColor, colorName, desc);
        tagDB.editTag(tag, new_tag);
        NavigationManager.switchFragment(new TagFragment(), getParentFragmentManager());
    }

    /**
     * A method to delete a tag in the database
     * @param   tag
     *      The tag to be deleted
     */
    public void deleteTag(Tag tag){
        tagDB.deleteTag(tag);
        NavigationManager.switchFragment(new TagFragment(), getParentFragmentManager());
    }


}
