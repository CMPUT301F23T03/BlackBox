package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class InventoryEditFragment extends Fragment {
    private EditText itemName;
    private EditText itemValue;
    private EditText itemDescription;

    public InventoryEditFragment(){}

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View editItemFragmentLayout = inflater.inflate(R.layout.edit_fragment, container, false);
        return editItemFragmentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // scroll view settings
        ScrollView sv = (ScrollView) view.findViewById(R.id.scrollView2);
        sv.post(new Runnable() {
            public void run() {
                sv.smoothScrollTo(0, 270);
            }
        });

        // get text fields
        itemName = view.findViewById(R.id.name_editText);
        itemValue = view.findViewById(R.id.value_editText);
        itemDescription = view.findViewById(R.id.desc_editText);

        // save an edited item by clicking the small add button
        Button small_save_button = view.findViewById(R.id.small_save_button);
        small_save_button.setOnClickListener(v -> {

            // get text field values as String
            String name = itemName.getText().toString();
            String value = itemValue.getText().toString();
            String desc = itemDescription.getText().toString();

            InventoryFragment fragment = new InventoryFragment();

            // pass in edited values to inventory fragment
            fragment.onItemEdited(name, value, desc);

            // switch to inventory fragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();


        });

        // back button - go back to inventory fragment
        Button backbutton = (Button) view.findViewById(R.id.back_button);
        backbutton.setOnClickListener((v) -> {
            InventoryFragment myFragment = new InventoryFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFragment, myFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // dropdown customization for tags and date
        // ...

    }
}
