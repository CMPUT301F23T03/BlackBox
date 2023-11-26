package com.example.blackbox.tag;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.blackbox.MainActivity;
import com.example.blackbox.NavigationManager;
import com.example.blackbox.R;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.tag.TagAdapter;
import com.example.blackbox.tag.TagAddFragment;
import com.example.blackbox.tag.TagDB;
import com.example.blackbox.tag.TagEditFragment;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
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
    private ListenerRegistration tagDBlistener;
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
        // enable navigation bar
        ((MainActivity) requireActivity()).toggleBottomNavigationView(true);
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
        tagDBlistener = tagDB.getTags()
                .orderBy("update_date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    int col = doc.getLong("color").intValue();
                    String desc = doc.getString("description");
                    String colorName = doc.getString("color_name");
                    String dbID = doc.getId();
                    Tag tag = new Tag(name, col, colorName, desc, dbID);
                    tag.setDateUpdatedWithString(doc.getString("update_date"));
                    tagList.add(tag);
                }
                // Notify the adapter that the data has changed
                tagAdapter.notifyDataSetChanged();
            }
        });
        final Button addButton = (Button) view.findViewById(R.id.add_tag_button);
        addButton.setOnClickListener((v) -> {
            TagAddFragment tagAddFragment = new TagAddFragment();
            NavigationManager.switchFragmentWithBack(tagAddFragment, getParentFragmentManager());
        });

        tagListView.setOnItemClickListener((parent, view1, position, id) -> {
            TagEditFragment tagEditFragment = TagEditFragment.newInstance(tagList.get(position));
            NavigationManager.switchFragmentWithBack(tagEditFragment, getParentFragmentManager());
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
        tagDBlistener.remove();
    }

}
