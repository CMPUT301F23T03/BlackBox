package com.example.blackbox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A fragment that represents the user profile screen. It displays user information,
 * allows the user to log out, and presents the user's name, email, and profile picture.
 */
public class ProfileFragment extends Fragment{
    private View view;
    private Button logOutButton;
    private Context activityContext;
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

        // Log out
        logOutButton = view.findViewById(R.id.logout_profile);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileDB.logOut();
                Intent i = new Intent(activityContext, GoogleSignInActivity.class);
                startActivity(i);
            }
        });

        // Display profile name
        TextView profileName = view.findViewById(R.id.name_profile);
        profileName.setText(profileDB.getName());

        // Display profile email
        TextView profileEmail = view.findViewById(R.id.username);
        profileEmail.setText(profileDB.getEmail());

        // Display profile picture
        ImageView profilePicture = view.findViewById(R.id.profile_pic);
        Uri imageUri = profileDB.getPhotoUrl();
        profilePicture.setImageURI(imageUri);
    }
}
