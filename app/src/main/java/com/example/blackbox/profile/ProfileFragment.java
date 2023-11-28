package com.example.blackbox.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.blackbox.utils.NavigationManager;
import com.example.blackbox.R;
import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.authentication.GoogleSignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * A fragment that represents the user profile screen. It displays user information,
 * allows the user to log out, and presents the user's name, email, bio, and profile picture.
 */
public class ProfileFragment extends Fragment{
    private View view;
    private Button logOutButton;
    private Button editButton;
    private Context activityContext;
    private BottomNavigationView bottomNavigationView;
    private GoogleAuthDB googleAuthDB = new GoogleAuthDB();
    private ProfileDB profileDB = new ProfileDB();

    /**
     * Default constructor for the ProfileFragment.
     */
    public ProfileFragment() {}

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
     * Called to create the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from
     *                           a previous saved state as given here.
     * @return                   The root view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.profile_fragment, container, false);

        // Display profile name, email and bio (taken from the database)
        updateDisplayedProfile(googleAuthDB.getUid());

        // Display profile picture (taken from the Google account)
        ImageButton profilePicture = view.findViewById(R.id.profile_pic);
        googleAuthDB.displayGoogleProfilePicture(profilePicture, 400, 400, this);

        return view;
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle)
     * has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from
     *                           a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Logout button
        logOutButton = view.findViewById(R.id.logout_profile);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleAuthDB.logOut();
                Intent i = new Intent(activityContext, GoogleSignInActivity.class);
                startActivity(i);
            }
        });

        // Edit profile button
        editButton = view.findViewById(R.id.edit_profile);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileEditFragment profileEditFragment = new ProfileEditFragment();
                NavigationManager.switchFragmentWithBack(profileEditFragment, getParentFragmentManager());
                Toast.makeText(activityContext, "LET'S EDIT", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the displayed profile information by retrieving data from the database.
     *
     * @param uid The user ID associated with the profile.
     */
    private void updateDisplayedProfile(String uid) {
        DocumentReference docRef = profileDB.getProfileRef().document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the value of the specified field
                        Object name = document.get("name");
                        Object bio = document.get("bio");
                        Object email = document.get("email");

                        // Display profile name
                        TextView profileName = view.findViewById(R.id.name_profile);
                        profileName.setText(name.toString());

                        // Display profile email
                        TextView profileEmail = view.findViewById(R.id.username);
                        profileEmail.setText(email.toString());

                        // Display profile bio
                        TextView profileBio = view.findViewById(R.id.description_profile);
                        profileBio.setText(bio.toString());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.w("TAG", "Error getting document", task.getException());
                }
            }
        });
    }
}
