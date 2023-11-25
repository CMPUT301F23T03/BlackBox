package com.example.blackbox.inventory;

import android.app.DatePickerDialog;
import android.content.Context;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.blackbox.AddEditFragment;
import com.example.blackbox.NavigationManager;
import com.example.blackbox.R;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.tag.TagDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;


/**
 * Fragments of this class allow the user to input information
 * relating to and item
 * This is an abstract class with two subclasses, one relating to adding items
 * and one related to editing items
 */
public abstract class InventoryAddEditFragment extends AddEditFragment {
    private EditText itemName;
    private EditText itemValue;
    private EditText itemDescription;
    private EditText itemMake;
    private EditText itemModel;
    private EditText itemSerialNumber;
    private EditText itemComment;
    private String name;
    private Double val;
    private String desc;
    private InventoryDB itemDB;
    private String make;
    private String model;
    private String serialNumber;
    private String comment;
    private Button dateButton;
    private final String dateFormat = "%d-%02d-%02d";

    private String date;
    private Context activityContext;
    private ArrayList<Tag> tags;
    private TextView tagDropdown;

    /**
     * Default constructor for the InventoryAddEditFragment
     */
    public InventoryAddEditFragment(int fragment_id){
        super(fragment_id);
    }


    /**
     * Called when the fragment is attached to an activity.
     *
     * @param context The context of the activity to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * A method which sets up the database, listeners,
     * and variables for the fragment
     * @param view
     *      The view from which to find UI elements
     */
    @Override
    public void setupFragment(View view) {
        // get database
        itemDB = new InventoryDB();

        // get text fields
        itemName = view.findViewById(R.id.name_editText);
        itemValue = view.findViewById(R.id.value_editText);
        itemDescription = view.findViewById(R.id.desc_editText);
        itemMake = view.findViewById(R.id.make_editText);
        itemModel = view.findViewById(R.id.model_editText);
        itemComment = view.findViewById(R.id.comment_editText);
        itemSerialNumber = view.findViewById(R.id.serial_number_editText);;

        tagDropdown = view.findViewById(R.id.tag_dropdown);
        tagDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTagSelectionDialog();
            }
        });


        setupBackButtonListener(view);

        // setup a date picker listener
        setupDatePickerListener(view);


    }


    /**
     * A method which sets up a listener to track whether the date field is pressed
     * @param view
     *      The view from which to find UI elements
     */
    public void setupDatePickerListener(View view){
        dateButton = view.findViewById(R.id.date_editText);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = String.format(dateFormat, currentYear, currentMonth + 1, currentDay);
        dateButton.setText(currentDate);
        dateButton.setOnClickListener(v -> {
            getDatePicker();
        });
    }


    /**
     * Gets information required from dateFiled in order to display the DatePicker
     */
    private void getDatePicker(){
        String dateString = dateButton.getText().toString();
        String[] dateParts = dateString.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int day = Integer.parseInt(dateParts[2]);
        showDatePicker(year, month, day);
    }

    /**
     * Displays a date picker
     * @param year
     *      The initial year to display
     * @param month
     *      The initial month to display
     * @param dayOfMonth
     *      The initial day to display
     */
    private void showDatePicker(int year, int month, int dayOfMonth) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(activityContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // This callback is called when the user selects a date.
                // You can update the EditText with the selected date.
                String selectedDate = String.format(dateFormat, year, month + 1, dayOfMonth); // Note: month is 0-based.
                dateButton.setText(selectedDate);
            }
        }, year, month, dayOfMonth); // Initial date (year, month, day). Month is 0-based.

        datePickerDialog.show();
    }

    /**
     * A method which sets up a listener to track whether the back button is pressed
     * @param view
     *      The view from which to find UI elements
     */
    public void setupBackButtonListener(View view){
        final Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
    }

    /**
     * A method which gets user input from the fragment and validates if it is correct
     * @return
     *      A Boolean representing whether the current user input is valid
     */
    public Boolean validateInput(){
        name = itemName.getText().toString();
        val = null;
        if (itemValue.getText().toString().length() > 0){
            val = Double.parseDouble(itemValue.getText().toString());
        }
        desc = itemDescription.getText().toString();
        make = itemMake.getText().toString();
        model = itemModel.getText().toString();
        serialNumber = itemSerialNumber.getText().toString();
        comment = itemComment.getText().toString();
        date = dateButton.getText().toString();

        if (name.length() == 0){
            Toast.makeText(getActivity(), "Name Required", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
        else if (val == null) {
            Toast.makeText(getActivity(), "Estimated Value Required", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
        else {
            // allow input
            return Boolean.TRUE;
        }
    }
    /**
     * A method which creates a new item and adds it to the database
     */
    @Override
    public void add(){

        TagDB tagDB = new TagDB();

        String[] selectedTagNames = tagDropdown.getText().toString().split(", ");
        tagDB.getAllTags(new TagDB.OnGetTagsCallback() {

            @Override
            public void onSuccess(ArrayList<Tag> tagList) {

                for (String selectedTagName : selectedTagNames) {
                    for (Tag tag : tagList) {
                        if (tag.getName().equals(selectedTagName)){
                            itemTags.add(tag);
                        }
                    }
                }
                Item new_item = new Item(name, itemTags, date, val, make, model, serialNumber, desc, comment);
                itemDB.addItemToDB(new_item);
                NavigationManager.switchFragmentWithBack(new InventoryFragment(), getParentFragmentManager());
            }
            @Override
            public void onError(String errorMessage) {
                // Handle the error, e.g., display an error message
                Log.e("InventoryAddEditFragment", "Error retrieving tag names: " + errorMessage);
            }
        });
    }

    /**
     * A method to replace an item in the database with a new one
     * @param item
     *      The item to be replaced
     */
    public void editItem(Item item){
        TagDB tagDB = new TagDB();

//        String[] selectedTagNames = tagDropdown.getText().toString().split(", ");
        ArrayList<Tag> itemTags = item.getTags();
        tagDB.getAllTags(new TagDB.OnGetTagsCallback() {

            @Override
            public void onSuccess(ArrayList<Tag> tagList) {

                for (Tag itemTag : itemTags) {
                    for (Tag tag : tagList) {
                        if (tag.getDataBaseID().equals(itemTag.getDataBaseID())){
                            itemTags.add(tag);
                        }
                    }
                }
                Item new_item = new Item(name, itemTags, date, val, make, model, serialNumber, desc, comment);
                itemDB.updateItemInDB(item, new_item);
                InventoryFragment inventoryFragment = new InventoryFragment();
                NavigationManager.switchFragmentWithBack(inventoryFragment, getParentFragmentManager());

            }
            @Override
            public void onError(String errorMessage) {
                // Handle the error, e.g., display an error message
                Log.e("InventoryAddEditFragment", "Error retrieving tag names: " + errorMessage);
            }
        });
    }

    /**
     * A method to delete an item in the database
     * @param  item
     *      The item to be deleted
     */
    public void deleteItem(Item item){
        itemDB.deleteItem(item);
        NavigationManager.switchFragmentWithBack(new InventoryFragment(), getParentFragmentManager());
    }

    /**
     * A method to adjust the fields to reflect the data from an item
     * @param item
     *           The item whose info will be used to fill fields
     */
    public void adjustFields(Item item){
        if (item.getName() != null) {
            itemName.setText(item.getName());
        }
        if (item.getDescription() != null){
            itemDescription.setText(item.getDescription());
        }
        if (item.getComment() != null){
            itemComment.setText(item.getComment());
        }
        if (item.getMake() != null){
            itemMake.setText(item.getMake());
        }
        if (item.getEstimatedValue() != null){
            itemValue.setText(item.getStringEstimatedValue());
        }
        if (item.getModel() != null){
            itemModel.setText(item.getModel());
        }
        if (item.getSerialNumber() != null){
            itemSerialNumber.setText(item.getSerialNumber());
        }
        if (item.getDateOfPurchase() != null){
            dateButton.setText(item.getDateOfPurchase());
        }
        ArrayList<Tag> selectedTags = null;
        if (item.getTags() != null){
             selectedTags = item.getTags();
        }
        if (selectedTags != null){
            ArrayList<String> selectedTagNames = new ArrayList<>();
            for (Tag tag : selectedTags) {
                selectedTagNames.add(tag.getName());
            }
            tagDropdown.setText(TextUtils.join(", ", selectedTagNames));
        }
    }

    /**
     * Creates a dialog box that prompts user to add or edit the tags of an item
     */
    private void showTagSelectionDialog() {
        TagDB tagDB = new TagDB();
        tagDB.getAllTags(new TagDB.OnGetTagsCallback() {
            @Override
            public void onSuccess(ArrayList<Tag> tagList) {
                boolean[] selectedTags = new boolean[tagList.size()];
                String[] tagNameList = new String[tagList.size()];

                // Ordering the tags in the dialog box
                Comparator<Tag> tagComp = new Comparator<Tag>() {
                    @Override
                    public int compare(Tag tag1, Tag tag2) {
                        int result = tag1.getName().compareToIgnoreCase(tag2.getName());
                        return result;
                    }
                };
                tagList.sort(tagComp);

                if (!tags.isEmpty()) {
                    for (int i = 0; i < tagList.size(); i++) {
                        for(Tag tag : tag)
                    }
                }

                if (tagList.size() > 0) {
                    for (int i = 0; i < tagList.size(); i++) {
                        tagNameList[i] = tagList.get(i).getName();
                    }

                    String[] current_itemTags = tagDropdown.getText().toString().split(", ");

                    for (int i = 0; i < tagNameList.length; i++) {
                        selectedTags[i] = Arrays.asList(current_itemTags).contains(tagNameList[i]);
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Select Tags");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(tagNameList, selectedTags, (dialogInterface, index, isChecked) -> {
                    // Update the selectedTags array when a tag is selected or deselected
                    selectedTags[index] = isChecked;
                });

                builder.setPositiveButton("OK", (dialogInterface, which) -> {
                    ArrayList<Tag> tags = new ArrayList<>();
                    for (int i = 0; i < selectedTags.length; i++) {
                        if (selectedTags[i]) {
                            tags.add(tagList.get(i));
                        }
                    }

                    // Update the UI to display the selected tags
                    ArrayList<String> selectedTagNames = new ArrayList<>();
                    for (Tag tag : tags) {
                        selectedTagNames.add(tag.getName());
                    }

                    tagDropdown.setText(TextUtils.join(", ", selectedTagNames));
                    dialogInterface.dismiss();
                });

                builder.setNegativeButton("Cancel", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                });

                builder.setNeutralButton("Clear All", (dialogInterface, which) -> {
                    for (int i = 0; i < selectedTags.length; i++) {
                        selectedTags[i] = false;
                    }
                    tagDropdown.setText("");
                });

                builder.show();
            }
            @Override
            public void onError(String errorMessage) {
                // Handle the error, e.g., display an error message
                Log.e("InventoryAddEditFragment", "Error retrieving tag names: " + errorMessage);
            }
        });
    }

}
