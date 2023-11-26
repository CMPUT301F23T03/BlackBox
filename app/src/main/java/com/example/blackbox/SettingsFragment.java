package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.blackbox.tag.TagFragment;
import com.example.blackbox.utils.NavigationManager;

public class SettingsFragment extends Fragment {
    View view;
    View tagsLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.settings_fragment, container, false);

        return view;
    }

    /**
     * Called when the activity is created. Initializes the main layout and sets up the BottomNavigationView.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tagsLayout = view.findViewById(R.id.tags_cl);
        tagsLayout.setOnClickListener(v -> {
            NavigationManager.switchFragmentWithBack(new TagFragment(), getParentFragmentManager());
        });
    }
}
