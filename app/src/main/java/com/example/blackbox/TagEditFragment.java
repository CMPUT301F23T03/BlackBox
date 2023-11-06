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
     * Provides fragment ID to parent class
     */
    public TagEditFragment(){
        super(R.layout.edit_tag_fragment);
    }
    /**
     * Create a new instance of the TagEditFragment with the provided Tag object as an argument.
     *
     * @param tag The index of Tag object to be associated with the fragment.
     * @return A new instance of TagEditFragment.
     */
    static TagEditFragment newInstance(Tag tag) {
        Bundle args = new Bundle();
        args.putSerializable("tagToEdit", tag);    // serialize Item object
        TagEditFragment fragment = new TagEditFragment();
        fragment.setArguments(args);    // set the Item object to be this fragment's argument
        return fragment;
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
        // fill in with data from tag passed as argument
        Tag tag = (Tag) getArguments().getSerializable("tagToEdit");
        adjustFields(tag);
        // set up on click listener for save button
        final Button saveButton = view.findViewById(R.id.save_tag_button);
        saveButton.setOnClickListener(v -> {
            if(validateInput()) {
                editTag(tag);
            }
        });
    }
}
