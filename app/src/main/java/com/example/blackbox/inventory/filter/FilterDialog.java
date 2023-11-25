package com.example.blackbox.inventory.filter;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackbox.R;
import com.example.blackbox.StringFormatter;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.inventory.ItemList;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.tag.TagAdapter;
import com.example.blackbox.tag.TagDB;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public abstract class FilterDialog{

    private static TextView accept;

    private static TextView cancel;

    private static CheckBox priceCheck;
    private static CheckBox dateCheck;
    private static CheckBox makeCheck;
    private static CheckBox tagCheck;
    private static EditText lowerRange;

    private static ConstraintLayout priceLayer;
    private static ConstraintLayout makeLayer;
    private static ConstraintLayout dateLayer;
    private static Spinner tagSpinner;

    private static EditText upperRange;
    private static DatePicker startDate;
    private static DatePicker endDate;
    private static EditText make;
    private static FragmentActivity activityContext;

    private static ItemList allItems;
    private static ArrayList<Item> filteredItems;

    private static ArrayAdapter<Item> inventoryAdapter;
    private static FilterListAdapter filterAdapter;
    private static TextView totalSumView;

    private static ArrayList<Tag> tags;
    private static TagDB tagDB;
    private static TagAdapter tagAdapter;

    private static void initializeSpinner(){
        FilterDialog.tagDB = new TagDB();
        tagDB.getAllTags(new TagDB.OnGetTagsCallback() {
            @Override
            public void onSuccess(ArrayList<Tag> tagList) {
                System.out.println(tagList);
                FilterDialog.tags = tagList;
                System.out.println(tags);
                System.out.println(tags.size());
                Log.d("TagFilter","Data received");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("TagFilter",errorMessage);
            }
        });
        tagAdapter = new TagAdapter(activityContext,FilterDialog.tags);
        tagSpinner.setAdapter(tagAdapter);
        System.out.println(tags);
        Log.d("TagSpinner","initialized with data");
    }

    private static void usePresetCheckBoxes(){
        if (FilterDialog.filterAdapter.getItemCount() == 0){
            return;
        }else{
            for (Filter filter: FilterDialog.filterAdapter.getFilterList()){
                switch (filter.getFilterType()){
                    case "make":
                        FilterDialog.makeCheck.setChecked(true);
                        make.setText(filter.getMake());
                        break;
                    case "date":
                        FilterDialog.dateCheck.setChecked(true);
                        String[] lowerDate = filter.getDateRange()[0].split("-");
                        String[] upperDate = filter.getDateRange()[1].split("-");
                        startDate.updateDate(Integer.parseInt(lowerDate[0]),Integer.parseInt(lowerDate[1]) - 1,Integer.parseInt(lowerDate[2]));
                        endDate.updateDate(Integer.parseInt(upperDate[0]),Integer.parseInt(upperDate[1]) - 1,Integer.parseInt(upperDate[2]));
                        break;
                    case "price":
                        FilterDialog.priceCheck.setChecked(true);
                        lowerRange.setText( filter.getPriceRange()[0]+ "");
                        upperRange.setText(filter.getPriceRange()[1]+ "");
                        break;
                    case "tag":
                        FilterDialog.tagCheck.setChecked(true);
                        break;
                }
            }
        }
    }

    private static void initializeCheckBoxes(){
        tagCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tagSpinner.setVisibility(View.VISIBLE);
                }else{
                    tagSpinner.setVisibility(View.GONE);
                }

            }
        });
        priceCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    priceLayer.setVisibility(View.VISIBLE);
                }else{
                    priceLayer.setVisibility(View.GONE);
                }
            }
        });
        dateCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dateLayer.setVisibility(View.VISIBLE);
                }else{
                    dateLayer.setVisibility(View.GONE);
                }
            }
        });
        makeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    makeLayer.setVisibility(View.VISIBLE);
                }else{
                    makeLayer.setVisibility(View.GONE);
                }
            }
        });
    }

    public static void showFilter(FragmentActivity activity, ItemList incomingList, ArrayAdapter<Item> inventoryAdapter, RecyclerView.Adapter filters){
        FilterDialog.inventoryAdapter = inventoryAdapter;
        FilterDialog.filterAdapter = (FilterListAdapter) filters;
        Log.d("FilterList",String.format("%d",FilterDialog.filterAdapter.getItemCount()));
        activityContext = activity;
        FilterDialog.totalSumView = activity.findViewById(R.id.total_sum);
        allItems = incomingList;
        filteredItems = new ArrayList<>();
        View view = activity.getLayoutInflater().inflate(R.layout.filter_dialog,null);
        priceCheck = view.findViewById(R.id.price_checkbox);
        dateCheck = view.findViewById(R.id.date_checkbox);
        makeCheck = view.findViewById(R.id.make_checkbox);
        tagCheck = view.findViewById(R.id.tag_checkbox);

        priceLayer = view.findViewById(R.id.price_range);
        makeLayer = view.findViewById(R.id.filter_make_layout);
        tagSpinner = view.findViewById(R.id.tag_selection_filter);
        dateLayer = view.findViewById(R.id.filter_date_range);

        accept = view.findViewById(R.id.accept_button);
        cancel = view.findViewById(R.id.cancel_button);
        lowerRange = view.findViewById(R.id.first_number);
        upperRange = view.findViewById(R.id.secondNumber);
        startDate = view.findViewById(R.id.start_date);
        endDate = view.findViewById(R.id.after_date);
        make = view.findViewById(R.id.make_edit_text);

        //initializeSpinner();
        initializeCheckBoxes();
        usePresetCheckBoxes();
        filterAdapter.clearFilters();


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
                    updateTotalSum();
                    FilterDialog.inventoryAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
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
        if (makeFilter.getItemList().size() > 0){
            makeFilter.setFilterName(String.format("\'%s\'",make));
            makeFilter.setMake(make);
            filterAdapter.addItem(makeFilter);
            filterAdapter.notifyDataSetChanged();
        }

    }

    private static void updateTotalSum(){
        Double totalSum = allItems.calculateTotalSum();
        totalSumView.setText("Total: " + StringFormatter.getMonetaryString(totalSum));

    }

    private static void filterByDate(int lowerBoundDay,int lowerBoundMonth,int lowerBoundYear, int upperBoundDay, int upperBoundMonth, int upperBoundYear){
        Filter dateFilter = new Filter("date");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lowerBound = String.format("%d-%d-%d",lowerBoundYear,lowerBoundMonth + 1,lowerBoundDay);
        String upperBound = String.format("%d-%d-%d",upperBoundYear,upperBoundMonth + 1,upperBoundDay);
        Date lowerBoundDate = new Date();
        Date upperBoundDate = new Date();
        try {
            lowerBoundDate = dateFormat.parse(lowerBound);
            upperBoundDate = dateFormat.parse(upperBound);
        }catch (Exception e){
            Log.d("FilterDialog","Dates are not correct");
        }
        for (Item item: allItems){
            Date date;
            try{
                date = dateFormat.parse(item.getDateOfPurchase());
                if (!date.equals(lowerBoundDate) && !date.equals(upperBoundDate)){
                    if (date.after(upperBoundDate) || date.before(lowerBoundDate)){
                        addToFilteredList(item,dateFilter);
                    }
                }

            }catch (Exception e){
                Log.d("filterByDate","Error occurred with parsing date for item");
            }
        }
        if (dateFilter.getItemList().size() > 0){
            dateFilter.setFilterName(String.format("%s - %s",lowerBound,upperBound));
            dateFilter.setDateRange(new String[]{lowerBound,upperBound});
            filterAdapter.addItem(dateFilter);
            filterAdapter.notifyDataSetChanged();
        }
    }

    private static void filterByValues(double lowerBound,double upperBound){
        Filter priceFilter = new Filter("price");
        for (Item item : allItems){
            double value = item.getEstimatedValue();
            if (value > upperBound || value < lowerBound) {
                addToFilteredList(item,priceFilter);
            }
        }
        if (priceFilter.getItemList().size() > 0){
            priceFilter.setFilterName(String.format("$%,.2f - $%,.2f",lowerBound,upperBound));
            priceFilter.setPriceRange(new double[]{lowerBound,upperBound});
            filterAdapter.addItem(priceFilter);
            filterAdapter.notifyDataSetChanged();
        }
    }

    private static void addToFilteredList(Item item,Filter filter){
        if (!filteredItems.contains(item)){
            filteredItems.add(item);
            filter.addItemToFilter(item);
        }
    }

    private static boolean handleSubmit(){
        double firstVal = 0;
        String todayDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            todayDate = LocalDate.now().toString();
        }
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

        if (dateLayer.getVisibility() == View.VISIBLE){
            filterByDate(lowerBoundDay,lowerBoundMonth,lowerBoundYear,upperBoundDay,upperBoundMonth,upperBoundYear);
        }
        if (priceLayer.getVisibility() == View.VISIBLE){
            filterByValues(firstVal,secondVal);
        }
        if (makeLayer.getVisibility() == View.VISIBLE){
            filterByMake(makeValue);
        }

        for (Item item: filteredItems){
            allItems.remove(item);
        }
        return true;
    }
}
