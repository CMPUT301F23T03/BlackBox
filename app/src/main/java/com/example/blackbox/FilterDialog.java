package com.example.blackbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLOutput;
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

    private static ArrayAdapter<Item> inventoryAdapter;
    private static FilterListAdapter filterAdapter;

    public static ArrayList<Item> showFilter(FragmentActivity activity, ArrayList<Item> incomingList, ArrayAdapter<Item> inventoryAdapter, RecyclerView.Adapter filters){
        FilterDialog.inventoryAdapter = inventoryAdapter;
        FilterDialog.filterAdapter = (FilterListAdapter) filters;
        activityContext = activity;
        allItems = incomingList;
        filteredItems = new ArrayList<>();
        View view = activity.getLayoutInflater().inflate(R.layout.filter_dialog,null);
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
                dialog.dismiss();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleSubmit()){

                    FilterDialog.inventoryAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        System.out.println("returning filtered list:" + filteredItems);
        return filteredItems;
    }

    private static int[] parseDate(String data){
        String[] dates = data.split("-");
        int[] dateInformation = {Integer.parseInt(dates[0]),Integer.parseInt(dates[1]),Integer.parseInt(dates[2])};
        return dateInformation;
    }

    private static void filterByMake(String make){
        Filter makeFilter = new Filter("make");
        for (Item item : allItems){
            String itemMake = item.getMake();
            if (itemMake != null) {
                if (!itemMake.contains(make)) {
                    addToFilteredList(item,makeFilter);
                }
            }else{
                addToFilteredList(item,makeFilter);
            }
        }
        filterAdapter.addItem(makeFilter);

    }
    private static void filterByDate(int lowerBoundDay,int lowerBoundMonth,int lowerBoundYear, int upperBoundDay, int upperBoundMonth, int upperBoundYear){
        Filter dateFilter = new Filter("date");
        for (Item item: allItems){
            String date = item.getDateOfPurchase();
            if (date != null) {
                int[] dateInformation = parseDate(item.getDateOfPurchase());
                int year = dateInformation[0];
                int month = dateInformation[1] - 1;
                int day = dateInformation[2];

                if (year > upperBoundYear || year < lowerBoundYear) {
                    addToFilteredList(item,dateFilter);
                } else{
                    if (month > upperBoundMonth || month < lowerBoundMonth){
                        addToFilteredList(item,dateFilter);
                    }else{
                        if (day > upperBoundDay || day < lowerBoundDay){
                            addToFilteredList(item,dateFilter);
                        }
                    }
                }
            }else{
                addToFilteredList(item,dateFilter);
            }
        }
        filterAdapter.addItem(dateFilter);
    }

    private static void filterByValues(double lowerBound,double upperBound){
        Filter priceFilter = new Filter("price");
        for (Item item : allItems){
            double value = item.getEstimatedValue();
            if (value > upperBound || value < lowerBound) {
                addToFilteredList(item,priceFilter);
            }
        }
        filterAdapter.addItem(priceFilter);
    }

    private static void addToFilteredList(Item item,Filter filter){
        if (!filteredItems.contains(item)){
            filteredItems.add(item);
            filter.addItemToFilter(item);
        }
    }

    private static boolean handleSubmit(){
        double firstVal = 0;
        double secondVal = Double.POSITIVE_INFINITY;
        String makeValue = make.getText().toString();
        int lowerBoundDay = startDate.getDayOfMonth();
        int lowerBoundMonth = startDate.getMonth();
        int lowerBoundYear = startDate.getYear();
        int upperBoundDay = endDate.getDayOfMonth();
        int upperBoundMonth = endDate.getMonth();
        int upperBoundYear = endDate.getYear();
        try{
            //check first the vals, and then the dated
            if (lowerRange.getText().toString().length() > 0){
                 firstVal = Double.parseDouble(lowerRange.getText().toString());
            }

            if (upperRange.getText().toString().length() > 0){
                secondVal = Double.parseDouble(upperRange.getText().toString());
            }

            //first filter by date, and then by price, and then by make
            if (lowerBoundYear <= upperBoundYear){
                if (lowerBoundMonth <= upperBoundMonth){
                    if (lowerBoundDay > upperBoundDay){
                        throw new IllegalArgumentException("End date must be after start date");
                    }
                }else{
                    throw new IllegalArgumentException("End date must be after start date");
                }
            }else{
                throw new IllegalArgumentException("End date must be after start date");
            }

            if (firstVal > secondVal){
                throw new NumberFormatException();
            }

        }catch(NumberFormatException  e){
            Toast.makeText(activityContext, "Upper Range (first) should be greater than Lower Range (second)", Toast.LENGTH_SHORT).show();
            return false;
        } catch (IllegalArgumentException e){
            Toast.makeText(activityContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        filterByDate(lowerBoundDay,lowerBoundMonth,lowerBoundYear,upperBoundDay,upperBoundMonth,upperBoundYear);
        if (firstVal != 0 ||secondVal != Double.POSITIVE_INFINITY){
            filterByValues(firstVal,secondVal);
            filterAdapter.notifyDataSetChanged();
        }
        if (makeValue != ""){
            filterByMake(makeValue);
            filterAdapter.notifyDataSetChanged();
        }

        System.out.println("No filter requirements broken");
        for (Item item: filteredItems){
            allItems.remove(item);
        }
        return true;
    }
}
