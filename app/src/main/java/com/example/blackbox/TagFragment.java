package com.example.blackbox;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private TagDB tagDB;
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



        // initialize tag list
        tagList = new ArrayList<>();
        tagListView = view.findViewById(R.id.tag_view);
        tagAdapter = new TagAdapter(activityContext, tagList);
        tagListView.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();

        // initialize database
        tagDB = new TagDB();
        // DB listener
        tagDB.getTags().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle any errors or exceptions
                    return;
                }
                tagList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    String name = doc.getString("name");
                    int val = doc.getLong("color").intValue();
                    String desc = doc.getString("description");
                    tagList.add(new Tag(name, val, desc));
                }
                // Notify the adapter that the data has changed
                tagAdapter.notifyDataSetChanged();
            }
        });
        final Button addButton = (Button) view.findViewById(R.id.add_tag_button);
        addButton.setOnClickListener((v) -> {
            TagAddFragment tagAddFragment = new TagAddFragment();
            loadFragment(tagAddFragment);
        });


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
    /**
     * Switch to a new fragment by replacing the current fragment in the layout container.
     *
     * @param fragment The new fragment to replace the current one.
     */
    private void loadFragment(Fragment fragment) {
        // create a FragmentManager from the support library
        FragmentManager fm =  getParentFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        // save the changes
        fragmentTransaction.commit();
    }
}