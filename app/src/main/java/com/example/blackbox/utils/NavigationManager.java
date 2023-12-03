package com.example.blackbox.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.blackbox.R;

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
    public static void switchFragmentWithBack(Fragment fragment, FragmentManager fm) {
        // Create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        // Set custom animations for the fragment transition
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_from_left, // enter animation
                R.anim.slide_out_to_right
        );

        // Replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        // allow the Android phone back button to be in-app functional
        fragmentTransaction.addToBackStack(null);
        // Save the changes
        fragmentTransaction.commit();
    }

    public static void switchFragmentWithoutBack(Fragment fragment, FragmentManager fm) {
        // Create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // Replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        // Clear backStack when unnecessary
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        // Save the changes
        fragmentTransaction.commit();
    }

}
