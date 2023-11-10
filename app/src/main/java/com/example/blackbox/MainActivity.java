package com.example.blackbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import android.view.MenuItem;

/**
 * The main activity of the application, responsible for handling the user interface and navigation.
 */
public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

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

        // load home page
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.inventory);
        NavigationManager.switchFragment(new InventoryFragment(), fm);

        // set a listener to handle item selection in the BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        Log.d("ItemId", String.format("%d",id));
                        if (id == R.id.inventory){
                            // load inventory fragment
                            NavigationManager.switchFragment(new InventoryFragment(), fm);
                        }
                        else if (id == R.id.expenses){
                            NavigationManager.switchFragment( new ExpenseFragment(), fm);
                        }
                        else if (id == R.id.scan){
                            // load scan fragment
                            NavigationManager.switchFragment(new ScanFragment(), fm);
                        }
                        else if (id == R.id.profile){
                            NavigationManager.switchFragment(new ProfileFragment(), fm);
                        }
                        else if (id == R.id.settings){
                            // load tag fragment
                            NavigationManager.switchFragment(new TagFragment(), fm);
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



}