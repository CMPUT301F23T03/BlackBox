package com.example.blackbox;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TagAddFragment extends Fragment {
    private EditText tagName;  // itemName text box
    private EditText tagColor; // itemValue text box
    private EditText tagDescription;   // itemDescription text box

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

        // An onclick listener for the add/save buttons
        View.OnClickListener saveOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.switchFragment(new TagFragment(), getParentFragmentManager());
            }
        };
        // attach the listener to the save/add buttons
        final Button bigSaveButton = view.findViewById(R.id.add_tag_button);
        final Button smallSaveButton = view.findViewById(R.id.small_save_button);
        bigSaveButton.setOnClickListener(saveOnClickListener);
        smallSaveButton.setOnClickListener(saveOnClickListener);
    }
}
