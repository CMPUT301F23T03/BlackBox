package com.example.blackbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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

    /**
     * Called when the activity is created. Initializes the main layout and sets up the BottomNavigationView.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a profile from Google Sign-In
        googleAuthDB = new GoogleAuthDB();
        googleAuthDB.createProfile();

        final FragmentManager fm = getSupportFragmentManager();

        // load home page
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.inventory);
        NavigationManager.switchFragmentWithBack(new InventoryFragment(), fm);

        // set a listener to handle item selection in the BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        Log.d("ItemId", String.format("%d",id));
                        if (id == R.id.inventory){
                            // load inventory fragment
                            NavigationManager.switchFragmentWithoutBack(new InventoryFragment(), fm);
                        }
                        else if (id == R.id.expenses){
                            NavigationManager.switchFragmentWithoutBack( new ExpenseFragment(), fm);
                        }
                        else if (id == R.id.scan){
                            // load scan fragment
                            NavigationManager.switchFragmentWithoutBack(new ScanFragment(), fm);
                        }
                        else if (id == R.id.profile){
                            NavigationManager.switchFragmentWithoutBack(new ProfileFragment(), fm);
                        }
                        else if (id == R.id.settings){
                            // load tag fragment
                            NavigationManager.switchFragmentWithoutBack(new SettingsFragment(), fm);
                        }
                        return true;
                    }
                }
        );


    }
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


    // Method to show/hide BottomNavigationView
    public void toggleBottomNavigationView(boolean isVisible) {
        if (isVisible) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }
}