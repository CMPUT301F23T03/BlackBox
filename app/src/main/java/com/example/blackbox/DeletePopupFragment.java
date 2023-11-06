package com.example.blackbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel button does nothing except close dialog
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
