package com.example.blackbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.authentication.GoogleSignInActivity;
import com.example.blackbox.profile.ProfileFragment;
import com.example.blackbox.tag.TagFragment;
import com.example.blackbox.utils.NavigationManager;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    View view;
    View tagsLayout;
    View logOutLayout;
    Spinner langSpinner;
    ArrayAdapter<String> langSpinAdapter;
    ImageButton profilePicture;
    Context activityContext;
    String selectedLanguage;
    private GoogleAuthDB googleAuthDB = new GoogleAuthDB();


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
        logOutLayout = view.findViewById(R.id.logout_cl);
        logOutLayout.setOnClickListener(v -> {
            googleAuthDB.logOut();
            Intent i = new Intent(activityContext, GoogleSignInActivity.class);
            startActivity(i);
        });
        langSpinner = view.findViewById(R.id.language_spinner);
        final String[] languages = {"English"};
        langSpinAdapter = new ArrayAdapter<String>(activityContext, android.R.layout.simple_spinner_item,languages);
        langSpinner.setAdapter(langSpinAdapter);
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item here
                selectedLanguage = langSpinAdapter.getItem(position);
                Log.d("onItemSelected", "Selected: " + selectedLanguage.toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });
    }
}
