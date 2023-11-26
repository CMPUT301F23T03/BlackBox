package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.profile.ProfileFragment;
import com.example.blackbox.tag.TagFragment;
import com.example.blackbox.utils.NavigationManager;

public class SettingsFragment extends Fragment {
    View view;
    View tagsLayout;
    ImageButton profilePicture;
    private GoogleAuthDB googleAuthDB = new GoogleAuthDB();


    /**
     * Called to create the view for the fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *      the view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // enable navigation bar
        ((MainActivity) requireActivity()).toggleBottomNavigationView(true);
        view =  inflater.inflate(R.layout.settings_fragment, container, false);

        // Display profile picture (taken from the Google account)
        profilePicture = view.findViewById(R.id.profile_button);
        googleAuthDB.displayGoogleProfilePicture(profilePicture, 80, 80, this);

        return view;
    }

    /**
     * Called when the activity is created. Initializes the main layout and sets up the BottomNavigationView.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tagsLayout = view.findViewById(R.id.tags_cl);
        tagsLayout.setOnClickListener(v -> {
            NavigationManager.switchFragmentWithBack(new TagFragment(), getParentFragmentManager());
        });
        profilePicture.setOnClickListener(v -> {
            NavigationManager.switchFragmentWithoutBack(new ProfileFragment(), getParentFragmentManager());
        });
    }
}
