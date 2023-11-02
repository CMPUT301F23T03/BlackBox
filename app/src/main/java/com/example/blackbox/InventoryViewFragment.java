package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class InventoryViewFragment extends Fragment {
    private EditText itemName;
    private EditText itemValue;
    private EditText itemDescription;

    public InventoryViewFragment(){}
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View viewItemFragmentLayout = inflater.inflate(R.layout.view_fragment, container, false);
        return viewItemFragmentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get text fields
        itemName = view.findViewById(R.id.name_editText);
        itemValue = view.findViewById(R.id.value_editText);
        itemDescription = view.findViewById(R.id.desc_editText);

        // add an item by clicking the small add button
        Button small_add_button = view.findViewById(R.id.small_add_button);
        small_add_button.setOnClickListener(v -> {

            // get text field values as String
            String name = itemName.getText().toString();
            String value = itemValue.getText().toString();
            String desc = itemDescription.getText().toString();

            // create an Item object and send it to inventory fragment
            Item new_item = new Item(name, Integer.parseInt(value), desc);
            InventoryFragment fragment = new InventoryFragment();
            fragment = InventoryFragment.newInstance(new_item);

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
