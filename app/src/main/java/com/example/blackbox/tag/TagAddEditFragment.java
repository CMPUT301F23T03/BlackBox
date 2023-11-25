package com.example.blackbox.tag;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blackbox.AddEditFragment;
import com.example.blackbox.NavigationManager;
import com.example.blackbox.R;

import java.util.ArrayList;

/**
 * The fragment responsible for handling user data input related to the
 * creation or editing of tags.
 */
public abstract class TagAddEditFragment extends AddEditFragment {
    private EditText tagName;  // itemName text box
    private EditText tagDescription;   // itemDescription text box
    private TagDB tagDB;
    private Integer selectedColor;     // the value of the currently selected color
    private String name;
    private String desc;
    private String colorName;
    private Spinner spinner;
    private ColorSpinnerAdapter spinnerAdapter;

    /**
     * Default constructor for the TagAddEditFragment.
     */
    public TagAddEditFragment(int fragment_id){
        super(fragment_id);
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
            getParentFragmentManager().popBackStack();
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
        if (name.length() > 0 && selectedColor != null) {
            // allow save action
            return Boolean.TRUE;
            }
        else{
            // display error message
            Toast.makeText(getActivity(), "Name Required", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
    }

    /**
     * A method which creates a new tag and adds it to the database
     */
    @Override
    public void add(){
        Tag new_tag = new Tag(name, selectedColor, colorName, desc);
        tagDB.addTagToDB(new_tag);
        NavigationManager.switchFragmentWithBack(new TagFragment(), getParentFragmentManager());
    }

    /**
     * A method to adjust the fields to reflect the data from a tag
     * @param tag
     *      The tag whose info will be used to fill fields
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
        NavigationManager.switchFragmentWithBack(new TagFragment(), getParentFragmentManager());
    }

    /**
     * A method to delete a tag in the database
     * @param   tag
     *      The tag to be deleted
     */
    public void deleteTag(Tag tag){
        tagDB.deleteTag(tag);
        NavigationManager.switchFragmentWithBack(new TagFragment(), getParentFragmentManager());
    }


}
