package com.example.blackbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

public abstract class FilterDialog {

    private Button cancelButton;
    private Button acceptFilters;
    public static void showFilter(FragmentActivity activity, int layoutId, int cancelId, int acceptId){
        System.out.println(activity);
        View view = activity.getLayoutInflater().inflate(layoutId,null);
        TextView accept = view.findViewById(acceptId);
        TextView cancel = view.findViewById(cancelId);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("Cancel Clicked");
                dialog.dismiss();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Apply clicked");
                dialog.dismiss();
            }
        });

//                .setPositiveButton(acceptId, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //do nothing
//            }
//        }).setNeutralButton(cancelId, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //do nothing
//                    }
//                })
//                .show()
//        ;
        dialog.show();
    }
}
