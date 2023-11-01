package com.example.blackbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        // Set a listener to handle item selection in the BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.menu_home){}

                        else if (id == R.id.menu_scan){
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
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        // Save the changes
        fragmentTransaction.commit();
    }
}