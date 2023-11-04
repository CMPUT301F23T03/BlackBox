package com.example.blackbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        // load home page
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.inventory);
        loadFragment(new InventoryFragment());
        
        // set a listener to handle item selection in the BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.inventory){
                            // load inventory fragment
                            loadFragment(new InventoryFragment());
                        }
                        else if (id == R.id.scan){
                            // load scan fragment
                            loadFragment(new ScanFragment());
                        }
                        return true;
                    }
                }
        );
    }

    /**
     * Load and replace a new Fragment within the given FrameLayout container.
     *
     * @param fragment The Fragment to be loaded and displayed.
     */
    private void loadFragment(Fragment fragment) {
        // create a FragmentManager from the support library
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        // save the changes
        fragmentTransaction.commit();
    }
}