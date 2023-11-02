package com.example.blackbox;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // InventoryFragment myFragment = new InventoryFragment();
        // FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // transaction.replace(R.id.contentFragment, myFragment);
        // transaction.addToBackStack(null);
        // transaction.commit();

        // moving between different sections happens here by controlling the bottom nav menu
        // ...
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.inventory);
        loadFragment(new InventoryFragment());
        
        // Set a listener to handle item selection in the BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.inventory){
                            // load scanFragment
                            loadFragment(new InventoryFragment());
                        }

                        else if (id == R.id.scan){
                            // load scanFragment
                            loadFragment(new ScanFragment());
                        }
                        // Add more cases for other menu items if needed
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
        // Create a FragmentManager from the support library
        FragmentManager fm = getSupportFragmentManager();
        // Create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // Replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        // Save the changes
        fragmentTransaction.commit();
    }
}