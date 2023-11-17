package com.example.blackbox;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * The `GoogleSignInActivity` class handles Google Sign-In using the One Tap API
 * and authenticates the user with Firebase Authentication. After successful authentication,
 * it updates the UI and navigates to the main activity.
 *
 * This class extends AppCompatActivity and implements the necessary methods
 * to handle the One Tap sign-in flow and Firebase authentication.
 */
public class GoogleSignInActivity extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    Button signInButton;


    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signInButton = findViewById(R.id.signin_button);
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();

        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == Activity.RESULT_OK) {
                    try {
                        SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(o.getData());
                        String idToken = googleCredential.getGoogleIdToken();
                        if (idToken !=  null) {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                            mAuth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(GoogleSignInActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d("message", "signInWithCredential:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                updateUI(user);
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w("message", "signInWithCredential:failure", task.getException());
                                                updateUI(null);
                                            }
                                        }
                                    });
//                            String email = googleCredential.getId();
//                            Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
                        }
                    } catch (ApiException e) {
                        switch (e.getStatusCode()) {
                            case CommonStatusCodes.CANCELED:
                                Log.d("message", "One-tap dialog was closed.");
                                // Don't re-prompt the user.
                                showOneTapUI = false;
                                break;
                            case CommonStatusCodes.NETWORK_ERROR:
                                Log.d("message", "One-tap encountered a network error.");
                                // Try again or just ignore.
                                break;
                            default:
                                Log.d("message", "Couldn't get credential from result."
                                        + e.getLocalizedMessage());
                                break;
                        }
                    }
                }
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(GoogleSignInActivity.this, new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                                activityResultLauncher.launch(intentSenderRequest);
                            }
                        })
                        .addOnFailureListener(GoogleSignInActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // No saved credentials found. Launch the One Tap sign-up flow, or
                                // do nothing and continue presenting the signed-out UI.
                                Log.d("message", e.getLocalizedMessage());
                            }
                        });
            }
        });
    }

    /**
     * Updates the user interface based on the authentication status.
     *
     * @param user The authenticated Firebase user. If null, indicates that the user is signed out.
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in
            String displayName = user.getDisplayName();
            Toast.makeText(this, "Welcome, " + displayName + "!", Toast.LENGTH_SHORT).show();
            // Start the new activity
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
        } else {
            // User is signed out
            Toast.makeText(this, "No user", Toast.LENGTH_SHORT).show();
        }
    }
}
