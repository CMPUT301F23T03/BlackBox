package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public abstract class AddEditFragment extends Fragment {
    private int fragment_id;

    /**
     * Default constructor for an AddEditFragment
     */
    public AddEditFragment(int fragment_id){
        this.fragment_id = fragment_id;
    }
    /**
     * Called to create the view for the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     * @return The view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View fragmentLayout = inflater.inflate(fragment_id, container, false);
        return fragmentLayout;
    }

    /**
     * This is a method to be defined by the subclass which will setup the major
     * elements of the fragment
     * @param view
     *      The view which the fragment will use for setup
     */
    public abstract void setupFragment(View view);

    /**
     * This is a method which will be responsible for adding an element
     * whatever that may entail in the specific instance
     */
    public abstract void add();
    /**
     * This is a method which validates the current user input and will
     * display an appropriate feedback message if the input is invalid
     * @return
     *      A Boolean representing if the current input is valid
     */
    public abstract Boolean validateInput();

    /**
     * A method which sets up a listener to track whether the back button is pressed
     * @param view
     *      The view from which to find UI elements
     */
    public abstract void setupBackButtonListener(View view);




}
