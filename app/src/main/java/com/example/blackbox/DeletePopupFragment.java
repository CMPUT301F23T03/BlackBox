package com.example.blackbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.blackbox.inventory.InventoryAddFragment;
import com.example.blackbox.inventory.Item;

/**
 * An instance of this class represents a popup which allows the user to
 * confirm or cancel a deletion action
 */
public class DeletePopupFragment extends AppCompatDialogFragment {


    private void onDeletionConfirmed() {
        // Set the result to be passed back to the TagEditFragment
        Bundle result = new Bundle();
        result.putBoolean("delete_confirmation", true);
        getParentFragmentManager().setFragmentResult("DELETE_RESULT_KEY", result);
    }

    private void onDeletionCanceled() {
        // Set the result to be passed back to the TagEditFragment
        Bundle result = new Bundle();
        result.putBoolean("delete_confirmation", false);
        getParentFragmentManager().setFragmentResult("DELETE_RESULT_KEY", result);
    }

    public static DeletePopupFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putSerializable("message", message);    // serialize Item object
        DeletePopupFragment fragment = new DeletePopupFragment();
        fragment.setArguments(args);    // set the Item object to be this fragment's argument
        return fragment;
    }

    /**
     * This constructs the dialog window for the popup fragment
     * when the fragment is created
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // inflate the layout for this fragment
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_delete_popup, null);

        Bundle arguments = getArguments();
        if (arguments != null) {
            // Arguments were provided when creating the fragment
            if (arguments.containsKey("message")) {
                final TextView messageView = view.findViewById(R.id.confirm_message);
                final String message = (String) arguments.getSerializable("message");
                messageView.setText(message);
                // "item" argument was provided
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onDeletionCanceled();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        onDeletionConfirmed();
                    }
                });

        return builder.create();
    }


}
