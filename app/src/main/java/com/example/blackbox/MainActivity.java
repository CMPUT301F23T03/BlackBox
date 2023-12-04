package com.example.blackbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.inventory.InventoryFragment;
import com.example.blackbox.profile.ProfileFragment;
import com.example.blackbox.scanBarcode.ScanFragment;
import com.example.blackbox.tag.TagFragment;
import com.example.blackbox.utils.NavigationManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * The main activity of the application, responsible for handling the user interface and navigation between main fragments.
 */
public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    GoogleAuthDB googleAuthDB;
    private boolean functionality_test_mode = false;
    String currentFragment;
    FrameLayout contentFragment;
    private boolean navBarHidden;



    public MainActivity(){};
    public MainActivity(boolean test){
        functionality_test_mode = test;
    };


    /**
     * Called when the activity is created. Initializes the main layout and sets up the BottomNavigationView.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fm = getSupportFragmentManager();
        // Create a profile from Google Sign-In if not in test mode
        if (!functionality_test_mode){
            googleAuthDB = new GoogleAuthDB();
            googleAuthDB.createProfile();

            // Initialize or restore the current fragment
            if (savedInstanceState != null){
                if (savedInstanceState.get("fragmentTag") == "Settings"){
                    NavigationManager.switchFragmentWithoutBack(new SettingsFragment(), fm);
                    currentFragment = "Settings";
                }
            }
            else{
                NavigationManager.switchFragmentWithoutBack(new InventoryFragment(), fm);
                currentFragment = "Inventory";
            }
        }


        // load home page
        contentFragment = findViewById(R.id.contentFragment);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.inventory);


        // set a listener to handle item selection in the BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    int id = item.getItemId();
                    Log.d("ItemId", String.format("%d",id));
                    if (id == R.id.inventory){
                        // load inventory fragment
                        currentFragment = "Inventory";
                        NavigationManager.switchFragmentWithoutBack(new InventoryFragment(), fm);
                    }
                    else if (id == R.id.expenses){
                        currentFragment = "Expenses";
                        NavigationManager.switchFragmentWithoutBack( new ExpenseFragment(), fm);
                    }
                    else if (id == R.id.scan){
                        // load scan fragment
                        currentFragment = "Scan";
                        NavigationManager.switchFragmentWithoutBack(new ScanFragment(), fm);
                    }
                    else if (id == R.id.profile){
                        currentFragment = "Profile";
                        NavigationManager.switchFragmentWithoutBack(new ProfileFragment(), fm);
                    }
                    else if (id == R.id.settings){
                        // load tag fragment
                        currentFragment = "Settings";
                        NavigationManager.switchFragmentWithoutBack(new SettingsFragment(), fm);

                    }
                    return true;
                }
        );

        setupKeyboardListener();


    }


    /**
     * A method to hide the nav bar when the keyboard
     */

    /**
     * Override method called when the app requests permissions at runtime,
     * and the user responds to the permission request.
     *
     * @param requestCode   The request code passed to requestPermissions().
     * @param permissions   The requested permissions. This array can contain one or more permissions.
     * @param grantResults  The grant results for the corresponding permissions in the permissions array.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Call the super method to ensure proper handling of permission results.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward the permission result to the associated fragment.
        // Replace R.id.contentFragment with the actual ID of your fragment container.
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.contentFragment);
        if (fragment != null) {
            // If the fragment is found, delegate the permission result handling to the fragment.
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * Method to show/hide BottomNavigationView
     * @param isVisible
     *          a boolean which represents whether to show "true" or hide "false" the bottom navigation view
     */
    public void toggleBottomNavigationView(boolean isVisible) {
        if (isVisible) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            navBarHidden = false;
        } else {
            bottomNavigationView.setVisibility(View.GONE);
            navBarHidden = true;

        }
    }

    /**
     * A method which describes how to save the state of the activity when being destroyed or recreated
     * @param outState
     *      Bundle in which to place the saved state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save relevant data to the outState bundle
        outState.putString("fragmentTag", currentFragment);
    }

    /**
     * A method which sets up a listener to hide the nav bar when the screen is partially obscured, such as by a keyboard
     */
    private void setupKeyboardListener(){
        // Define a threshold for the height difference
        int keyboardHeightThreshold = 500;
        // Get the root view of your layout
        final View rootView = findViewById(android.R.id.content);

        // Add a global layout listener to the root view
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // only check when the nav bar is not already hidden for the fragment
                if (!navBarHidden) {
                    // This method will be called whenever the layout changes, including when the keyboard is opened or closed

                    // Get the height of the visible part of the root view
                    int screenBottom = rootView.getBottom();

                    // Get the height of the entire root view
                    int navigationViewBottom = contentFragment.getBottom();

                    // Calculate the height difference
                    int heightDiff = screenBottom - navigationViewBottom;



                    // Check if the height difference exceeds the threshold
                    if (heightDiff > keyboardHeightThreshold) {
                        bottomNavigationView.setVisibility(View.GONE);
                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}