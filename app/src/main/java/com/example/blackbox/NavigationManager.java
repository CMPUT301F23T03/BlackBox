package com.example.blackbox;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
/**
 * The `NavigationManager` class provides a utility method for swapping out the currently displayed fragment
 * with a new one. The `switchFragment` method takes a `Fragment` and a `FragmentManager` as parameters,
 * allowing the transaction to be processed and the new fragment to be displayed.
 */
public class NavigationManager {
    /**
     * This method swaps out the currently displayed fragment for a new one
     * @param fragment
     *          the fragment to be displayed
     * @param fm
     *          a fragment manager which allows the transaction to be processed
     */
    public static void switchFragment(Fragment fragment, FragmentManager fm) {
        // Create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // Replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        // Save the changes
        fragmentTransaction.commit();
    }

}
