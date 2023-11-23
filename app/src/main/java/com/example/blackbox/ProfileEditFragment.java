package com.example.blackbox;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileEditFragment extends Fragment {
    private Context activityContext;
    private Button saveButton;
    private Button backButton;
    private GoogleAuthDB googleAuthDB;
    private ProfileDB profileDB;

    /**
     * Default constructor for an ProfileEditFragment
     */
    public ProfileEditFragment() {
        googleAuthDB = new GoogleAuthDB();
        profileDB = new ProfileDB();
    }

    /**
     * Called to create the view for the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     * @return The view for the fragment.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.profile_edit_fragment, container, false);
        return fragmentLayout;
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
     * Called when the fragment's view has been created. Handles user interactions for editing profile.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Display pre-defined values on the text boxes
        EditText editTextName = view.findViewById(R.id.name_editText);
        EditText editTextBio = view.findViewById(R.id.bio_editText);
        profileDB.getProfileById(googleAuthDB.getUid(), new ProfileDB.OnProfileRetrievedListener() {
            @Override
            public void OnProfileRetrievedListener(Profile profile) {
                if (profile != null) {
                    editTextName.setText(profile.getName());
                    editTextBio.setText(profile.getBio());
                } else {
                    Log.d("TAG", "Profile not found or an error occurred");
                }
            }
        });

        // Add text watchers for the text boxes to ensure that character limit is not exceeded
        int maxCharName = 20;
        int maxCharBio = 50;
        EditTextCharacterLimitWatcher nameTextWatcher = new EditTextCharacterLimitWatcher(editTextName, maxCharName);
        editTextName.addTextChangedListener(nameTextWatcher);
        EditTextCharacterLimitWatcher bioTextWatcher = new EditTextCharacterLimitWatcher(editTextBio, maxCharBio);
        editTextBio.addTextChangedListener(bioTextWatcher);

        // When user clicks save button
        saveButton = view.findViewById(R.id.small_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String nameText = editTextName.getText().toString();
                String bioText = editTextBio.getText().toString();

                // Modify the Profile object with the same uid in the DB
                Profile newProfile = new Profile(googleAuthDB.getUid(), nameText, bioText, googleAuthDB.getEmail());
                profileDB.addEditProfile(newProfile);

                // Go back to the ProfileFragment view
                ProfileFragment profileFragment = new ProfileFragment();
                NavigationManager.switchFragment(profileFragment, getParentFragmentManager());
            }
        });

        // When back button is clicked
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the ProfileFragment view
                ProfileFragment profileFragment = new ProfileFragment();
                NavigationManager.switchFragment(profileFragment, getParentFragmentManager());
            }
        });
    }
}
