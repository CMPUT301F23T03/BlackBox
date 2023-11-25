package com.example.blackbox.inventory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.blackbox.DeletePopupFragment;
import com.example.blackbox.R;

/**
 * A Fragment responsible for editing an inventory item's details.
 */
public class InventoryEditFragment extends InventoryAddEditFragment {

    /**
     * Default constructor for the InventoryEditFragment.
     */
    private Item item;
    public InventoryEditFragment(){
        super(R.layout.edit_fragment);
    }

    /**
     * Create a new instance of the InventoryEditFragment with the provided Item object as an argument.
     *
     * @param item The Item object to be associated with the fragment.
     * @return A new instance of InventoryEditFragment.
     */
    static InventoryEditFragment newInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable("item", item);    // serialize Item object
        InventoryEditFragment fragment = new InventoryEditFragment();
        fragment.setArguments(args);    // set the Item object to be this fragment's argument
        return fragment;
    }


    /**
     * Called when the fragment's view has been created. Handles user interactions for editing an item.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the index of item to be edited
        item = (Item) getArguments().getSerializable("item");

        setupFragment(view);
        adjustFields(item);

        // save an edited item by clicking the small add button
        Button small_save_button = view.findViewById(R.id.small_save_button);
        small_save_button.setOnClickListener(v -> {
            if(validateInput()){
                editItem(item);
            }
        });

        // setup a delete button
        final Button deleteButton = view.findViewById(R.id.delete_item_button);
        deleteButton.setOnClickListener(v -> {
            showDeletePopup();
        });

    }
    /**
     * Display a confirmation dialog for deleting an item
     */
    private void showDeletePopup(){
        DeletePopupFragment confirmationPopup = new DeletePopupFragment();
        getParentFragmentManager().setFragmentResultListener("DELETE_RESULT_KEY", this, (requestKey, result) -> {
            if (requestKey.equals("DELETE_RESULT_KEY")) {
                // Handle the result here
                boolean deleted = result.getBoolean("delete_confirmation", false);
                if (deleted) {
                    deleteItem(item);
                }
            }
        });
        confirmationPopup.show(getParentFragmentManager(), "DELETE_TAG");
    }
}
