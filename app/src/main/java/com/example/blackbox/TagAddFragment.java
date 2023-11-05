package com.example.blackbox;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class TagAddFragment extends Fragment {
    private EditText tagName;  // itemName text box
    private EditText tagDescription;   // itemDescription text box
    private TagDB tagDB;
    private Integer selectedColor;     // the value of the currently selected color

    /**
     * Default constructor for the TagAddFragment.
     */
    public TagAddFragment(){}

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
        View addItemFragmentLayout = inflater.inflate(R.layout.add_tag_fragment, container, false);
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

        final Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            NavigationManager.switchFragment(new TagFragment(), getParentFragmentManager());
        });


        // init DB
        tagDB = new TagDB();

        tagName = view.findViewById(R.id.name_editText);
        tagDescription = view.findViewById(R.id.desc_editText);

        // An onclick listener for the add/save buttons
        View.OnClickListener saveOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    saveTag();
            }
        };
        // attach the listener to the save/add buttons
        final Button bigSaveButton = view.findViewById(R.id.add_tag_button);
        final Button smallSaveButton = view.findViewById(R.id.small_save_button);
        bigSaveButton.setOnClickListener(saveOnClickListener);
        smallSaveButton.setOnClickListener(saveOnClickListener);

        // populate spinner
        Spinner spinner = view.findViewById(R.id.color_spinner);
        ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(getActivity(), new ArrayList<TagColor>());
        adapter.populateColors(getResources());
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item here
                selectedColor = adapter.getItem(position).getColor();
                Log.d("onItemSelected", "Selected: " + selectedColor.toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });
    }

    public void saveTag(){
        // Get text field values as String
        String name = tagName.getText().toString();
        String desc = tagDescription.getText().toString();
        if (name.length() > 0 && selectedColor != null && desc.length() > 0) {

            // Create an Tag object and add it to the DB
            Tag new_tag = new Tag(name, selectedColor, desc);
            tagDB.addTagToDB(new_tag);

            // switch back to tag fragment
            NavigationManager.switchFragment(new TagFragment(), getParentFragmentManager());
        }
        else{
            // display error message
            Toast.makeText(getActivity(), "Missing Information", Toast.LENGTH_SHORT).show();
        }
    }
}
