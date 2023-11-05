package com.example.blackbox;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * A fragment responsible for viewing, editing, and adding tags
 */
public class TagFragment extends Fragment {
    private ArrayList<Tag> tagList;
    private ListView tagListView;
    private ArrayAdapter<Tag> tagAdapter;
    private View view;
    private Context activityContext;       // context of MainActivity

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

        // initialize database
        tagList = new ArrayList<>();
        tagListView = view.findViewById(R.id.tag_view);
        tagAdapter = new TagAdapter(activityContext, tagList);
        tagListView.setAdapter(tagAdapter);

        tagList.add(new Tag("Tag 1", "#FF5733"));
        tagList.add(new Tag("Tag 2", "#FF1230"));
        tagAdapter.notifyDataSetChanged();
    }
    /**
     * Called when the fragment is attached to an activity.
     *
     * @param context The context of the activity to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * Called when the fragment is detached from the activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        activityContext = null;
    }

}
