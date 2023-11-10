package com.example.blackbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public abstract class FilterDialog {

    private static TextView accept;

    private static TextView cancel;

    private static EditText lowerRange;
    private static EditText upperRange;
    private static DatePicker startDate;
    private static DatePicker endDate;
    private static EditText make;
    private static FragmentActivity activityContext;

    private static ArrayList<Item> allItems;
    private static ArrayList<Item> filteredItems;

    public static ArrayList<Item> showFilter(FragmentActivity activity, int layoutId, ArrayList<Item> incomingList){
        activityContext = activity;
        allItems = incomingList;
        filteredItems = new ArrayList<>();
        View view = activity.getLayoutInflater().inflate(layoutId,null);
        accept = view.findViewById(R.id.accept_button);
        cancel = view.findViewById(R.id.cancel_button);
        lowerRange = view.findViewById(R.id.first_number);
        upperRange = view.findViewById(R.id.secondNumber);
        startDate = view.findViewById(R.id.start_date);
        endDate = view.findViewById(R.id.after_date);
        make = view.findViewById(R.id.make_edit_text);


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
                if (handleSubmit()){
                    dialog.dismiss();

                }
            }
        });

        dialog.show();
        return filteredItems;
    }

    private static int[] parseDate(String data){
        String[] dates = data.split("-");
        int[] dateInformation = {Integer.parseInt(dates[0]),Integer.parseInt(dates[0]),Integer.parseInt(dates[2])};
        return dateInformation;
    }

    private static void filterByMake(String make){
        for (Item item : filteredItems){
            if (!item.getMake().contains(make) || !item.getModel().contains(make)){
                filteredItems.remove(item);
            }
        }
    }
    private static void filterByDate(int lowerBoundDay,int lowerBoundMonth,int lowerBoundYear, int upperBoundDay, int upperBoundMonth, int upperBoundYear){
        for (Item item: allItems){
            int[] dateInformation = parseDate(item.getDateOfPurchase());
            int year = dateInformation[0];
            int month = dateInformation[1] - 1;
            int day = dateInformation[2];

            if (year <= upperBoundYear && year >= lowerBoundYear){
                if (month <= upperBoundMonth && month >= lowerBoundMonth){
                    if (day <= upperBoundDay && day >= lowerBoundDay){
                        filteredItems.add(item);
                    }
                }
            }
        }
    }

    private static void filterByValues(double lowerBound,double upperBound){
        for (Item item : allItems){
            double value = item.getEstimatedValue();
            if (value > upperBound || value < lowerBound) {
                filteredItems.remove(item);
            }
        }
    }

    private static boolean handleSubmit(){
        try{
            //check first the vals, and then the dated
            double firstVal = Double.parseDouble(lowerRange.getText().toString());
            double secondVal = Double.parseDouble(upperRange.getText().toString());
            String makeValue = make.getText().toString();
            int lowerBoundDay = startDate.getDayOfMonth();
            int lowerBoundMonth = startDate.getMonth();
            int lowerBoundYear = startDate.getYear();
            int upperBoundDay = endDate.getDayOfMonth();
            int upperBoundMonth = endDate.getMonth();
            int upperBoundYear = endDate.getYear();


            //first filter by date, and then by price, and then by make
            if (lowerBoundYear <= upperBoundYear){
                if (lowerBoundMonth <= upperBoundMonth){
                    if (lowerBoundDay > upperBoundDay){
                        throw new IllegalArgumentException("End date must be after start date");
                    }else{
                        filterByDate(lowerBoundDay,lowerBoundMonth,lowerBoundYear,upperBoundDay,upperBoundMonth,upperBoundYear);
                    }
                }else{
                    throw new IllegalArgumentException("End date must be after start date");
                }
            }else{
                throw new IllegalArgumentException("End date must be after start date");
            }

            if (firstVal > secondVal){
                throw new NumberFormatException();
            }else{
                filterByValues(firstVal,secondVal);
            }

            filterByMake(makeValue);


        }catch(NumberFormatException  e){
            Toast.makeText(activityContext, "Upper Range (first) should be greater than Lower Range (second)", Toast.LENGTH_SHORT).show();
            return false;
        }catch (NullPointerException e){
            Toast.makeText(activityContext, "Upper Range (first) should be greater than Lower Range (second)", Toast.LENGTH_SHORT).show();
            return false;
        }catch (IllegalArgumentException e){
            Toast.makeText(activityContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        for (Item item: filteredItems){
            allItems.remove(item);
        }
        return true;
    }
}
