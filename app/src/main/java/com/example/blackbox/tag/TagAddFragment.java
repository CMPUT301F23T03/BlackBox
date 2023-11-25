package com.example.blackbox.tag;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.blackbox.R;

/**
 * A fragment responsible for adding tags
 */
public class TagAddFragment extends TagAddEditFragment {


    /**
     * Default constructor for the TagAddFragment.
     * Provides fragment ID to parent class
     */
    public TagAddFragment(){
        super(R.layout.add_tag_fragment);
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

        setupFragment(view);

        // An onclick listener for the add/save buttons
        View.OnClickListener saveOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if current input is valid add a new tag
                if(validateInput()){
                    add();
                };
            }
        };
        // attach the listener to the save/add buttons
        final Button bigSaveButton = view.findViewById(R.id.add_tag_button);
        final Button smallSaveButton = view.findViewById(R.id.small_save_button);
        bigSaveButton.setOnClickListener(saveOnClickListener);
        smallSaveButton.setOnClickListener(saveOnClickListener);
    }
}
