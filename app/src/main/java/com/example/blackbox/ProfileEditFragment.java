package com.example.blackbox;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class ProfileEditFragment extends AddEditFragment {

    private Context activityContext;

    public ProfileEditFragment() {
        super(R.layout.profile_edit_fragment);
    }

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

    @Override
    public void setupFragment(View view) {

    }

    @Override
    public void add() {

    }

    @Override
    public Boolean validateInput() {
        return null;
    }

    @Override
    public void setupBackButtonListener(View view) {

    }
}
