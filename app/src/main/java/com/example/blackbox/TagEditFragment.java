package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TagEditFragment extends TagAddEditFragment {
    /**
     * Default constructor for the TagEditFragment.
     */
    public TagEditFragment(){
        super(R.layout.edit_tag_fragment);
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
        final Button saveButton = view.findViewById(R.id.save_tag_button);
        saveButton.setOnClickListener(v -> {
            Boolean valid = validateInput(v);
        });
    }
}
