package com.example.blackbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

public abstract class FilterDialog {

    private Button cancelButton;
    private Button acceptFilters;
    public static void showFilter(FragmentActivity activity, int layoutId, int cancelId, int acceptId){
        View view = activity.getLayoutInflater().inflate(layoutId,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view)
                .setPositiveButton(acceptId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        }).setNeutralButton(cancelId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .show()
        ;





    }
}
