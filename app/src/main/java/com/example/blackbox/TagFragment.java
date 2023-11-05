package com.example.blackbox;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A fragment responsible for viewing, editing, and adding tags
 */
public class TagFragment extends Fragment {
    View view;
    /**
     * Called to create the view for the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     * @return The view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.tag_fragment, container, false);


        return view;
    }
    /**
     * Called when the fragment's view has been created.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
