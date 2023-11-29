package com.example.blackbox;

import static android.content.Intent.getIntent;
import static android.content.Intent.parseUri;
import static androidx.core.app.ActivityCompat.recreate;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.authentication.GoogleSignInActivity;
import com.example.blackbox.inventory.InventoryDB;
import com.example.blackbox.profile.ProfileFragment;
import com.example.blackbox.tag.TagDB;
import com.example.blackbox.tag.TagFragment;
import com.example.blackbox.utils.NavigationManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SettingsFragment extends Fragment {
    View view;
    View tagsLayout;
    View logOutLayout;
    Spinner langSpinner;
    View resetInventoryLayout;
    ArrayAdapter<String> langSpinAdapter;
    ImageButton profilePicture;
    Context activityContext;
    String selectedLanguage;
    ToggleButton darkModeButton;

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
        // setup tags button
        tagsLayout = view.findViewById(R.id.tags_cl);
        tagsLayout.setOnClickListener(v -> {
            NavigationManager.switchFragmentWithBack(new TagFragment(), getParentFragmentManager());
        });
        // setup profile picture
        profilePicture.setOnClickListener(v -> {
            NavigationManager.switchFragmentWithoutBack(new ProfileFragment(), getParentFragmentManager());
        });
        // setup logout button
        logOutLayout = view.findViewById(R.id.logout_cl);
        logOutLayout.setOnClickListener(v -> {
            googleAuthDB.logOut();
            Toast.makeText(activityContext, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(activityContext, GoogleSignInActivity.class);
            startActivity(i);
        });
        // setup language selector
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
        // setup reset button
        resetInventoryLayout = view.findViewById(R.id.reset_cl);
        resetInventoryLayout.setOnClickListener(v -> {
            showResetPopup();
        });
        // setup dark mode button
        darkModeButton = view.findViewById(R.id.dark_mode_button);
        // check if dark mode is active
        if (isDarkModeActive()){
            darkModeButton.setChecked(true);
        }

        darkModeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Enable dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    // reload fragment
//                    NavigationManager.switchFragmentWithoutBack(new SettingsFragment(), getParentFragmentManager());
                } else {
                    // Disable dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    // reload fragment
//                    NavigationManager.switchFragmentWithoutBack(new SettingsFragment(), getParentFragmentManager());
                }
                MainActivity mainActivity = (MainActivity) getActivity();
            }
        });

    }

    /**
     * Checks whether dark mode is active
     * @return
     *      a boolean, True if dark mode is active, false otherwise.
     */
    private boolean isDarkModeActive() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }


    /**
     * Display a confirmation dialog for resetting inventory
     */
    private void showResetPopup(){
        final String message = "Are you sure you would like to reset the inventory?\n THIS ACTION IS PERMANENT!";
        DeletePopupFragment confirmationPopup = DeletePopupFragment.newInstance(message);
        getParentFragmentManager().setFragmentResultListener("DELETE_RESULT_KEY", this, (requestKey, result) -> {
            if (requestKey.equals("DELETE_RESULT_KEY")) {
                // Handle the result here
                boolean deleted = result.getBoolean("delete_confirmation", false);
                if (deleted) {
                    Log.d("InventoryReset", "Inventory Reset Initialized");
                    resetInventory();
                }
                else{
                    Log.d("InventoryReset", "Inventory Reset Cancelled");
                }
            }
        });
        confirmationPopup.show(getParentFragmentManager(), "DELETE_TAG");
    }

    private void resetInventory(){
        CollectionReference inventory = new InventoryDB().getInventory();
        // get all items from the user
        inventory.whereEqualTo("user_id",googleAuthDB.getUid()).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               QuerySnapshot itemSnapshot = (QuerySnapshot) task.getResult();
               // delete all items
               List<Task<Void>> deleteItemsTasks = new ArrayList<>();
               for(QueryDocumentSnapshot item: itemSnapshot){
                   deleteItemsTasks.add(inventory.document(item.getId()).delete());
               }
               // delete tags once all items are deleted
               if (deleteItemsTasks.size() == 0){
                   resetTags();
               }
               else{
                   Tasks.whenAll(deleteItemsTasks).addOnCompleteListener(task1 -> {
                       if (task1.isSuccessful()){
                           resetTags();
                       }
                       // check if all items cannot be deleted
                       else {
                           Exception e = task.getException();
                           if (e != null){
                               Log.e("Firestore", "resetInventory failed: some items could not be deleted", e);
                           }
                       }
                   });
               }
           }
           // check if items could not be received
           else{
               Exception e = task.getException();
               if (e != null){
                   Log.e("Firestore", "resetInventory failed: could not fetch items", e);
               }
           }
        });
    }
    private void resetTags(){
        CollectionReference tagCollection = new TagDB().getTags();
        tagCollection.whereEqualTo("user_id",googleAuthDB.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot itemSnapshot = (QuerySnapshot) task.getResult();
                // delete all items
                List<Task<Void>> deleteItemsTasks = new ArrayList<>();
                for(QueryDocumentSnapshot item: itemSnapshot){
                    deleteItemsTasks.add(tagCollection.document(item.getId()).delete());
                }
                // log success
                if (deleteItemsTasks.size() == 0){
                    Log.d("Firestore", "Inventory Reset!");
                }
                else{
                    Tasks.whenAll(deleteItemsTasks).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            Log.d("Firestore", "Inventory Reset!");
                        }
                        // check if all items cannot be deleted
                        else {
                            Exception e = task.getException();
                            if (e != null){
                                Log.e("Firestore", "resetInventory failed: some tags could not be deleted", e);
                            }
                        }
                    });
                }
            }
            // check if items could not be received
            else{
                Exception e = task.getException();
                if (e != null){
                    Log.e("Firestore", "resetInventory failed: could not fetch tags", e);
                }
            }
        });
    }

}
