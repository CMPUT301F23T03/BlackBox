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

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class TagAddEditFragment extends Fragment {
    private EditText tagName;  // itemName text box
    private EditText tagDescription;   // itemDescription text box
    private TagDB tagDB;
    private Integer selectedColor;     // the value of the currently selected color
    private String name;
    private String desc;
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


    public void setupBackButtonListener(View view){
        final Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            NavigationManager.switchFragment(new TagFragment(), getParentFragmentManager());
        });
    }

    public void setupColorSpinnerListener(View view){
        // populate spinner
        final Spinner spinner = view.findViewById(R.id.color_spinner);
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
    public Boolean validateInput(View view){
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
}
