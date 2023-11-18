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
 * A planned fragment representing the user profile
 */
public class ProfileFragment extends Fragment{
    private View view;
    private Button logOutButton;
    private Context activityContext;
    private ProfileDB profileDB = new ProfileDB();


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.profile_fragment, container, false);
        return view;
    }

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
