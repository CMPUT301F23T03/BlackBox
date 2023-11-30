package com.example.blackbox.inventory;

import android.app.DatePickerDialog;
import android.content.Context;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackbox.AddEditFragment;

import com.example.blackbox.inventory.AttachImageFragment;
import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.ImageRecyclerAdapter;
import com.example.blackbox.utils.NavigationManager;

import com.example.blackbox.R;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.tag.TagDB;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;


/**
 * Fragments of this class allow the user to input information
 * relating to and item
 * This is an abstract class with two subclasses, one relating to adding items
 * and one related to editing items
 */
public abstract class InventoryAddEditFragment extends AddEditFragment implements AttachImageFragment.OnImageSelectedListener {
    // EditText fields for various item details
    private EditText itemName;         // Holds the item name
    private EditText itemValue;        // Holds the item value
    private EditText itemDescription;  // Holds the item description
    private EditText itemMake;         // Holds the item make information
    private EditText itemModel;        // Holds the item model information
    private EditText itemSerialNumber; // Holds the item serial number
    private EditText itemComment;      // Holds additional comments related to the item

    // Variables to store item details
    private String name;               // Stores the item name
    private Double val;                // Stores the item value
    private String desc;               // Stores the item description
    protected InventoryDB itemDB;      // Database reference for inventory management
    private String make;               // Stores the item make information
    private String model;              // Stores the item model information
    private String serialNumber;       // Stores the item serial number
    private String comment;            // Stores additional comments related to the item

    // Button to handle date selection
    private Button dateButton;         // Button for selecting date related to the item
    private final String dateFormat = "%d-%02d-%02d";  // Format for date display

    // Context reference for the current activity
    private String date;               // Stores the selected date in the specified format
    private Context activityContext;   // Context reference for the current activity

    // List of tags associated with the item
    private ArrayList<Tag> tags = new ArrayList<>();          // Holds all available tags
    private TextView tagDropdown;       // TextView to display and select tags
    ArrayList<Tag> selectedTags = new ArrayList<>();           // Holds selected tags

    // Button to add images to the item
    private ImageButton addImgBtn;     // Button for adding images to the item
    private AttachImageFragment attachImageFragment = new AttachImageFragment();  // Fragment for attaching images

    // RecyclerView and adapter for displaying images
    private RecyclerView recyclerView;             // RecyclerView to display attached images
    protected ArrayList<Uri> displayedUris;       // List of URIs of displayed images
    protected ImageRecyclerAdapter adapter;        // Adapter for populating images in RecyclerView



    private GoogleAuthDB googleAuthDB = new GoogleAuthDB();


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
        itemSerialNumber = view.findViewById(R.id.serial_number_editText);
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
        // add image
        recyclerView = view.findViewById(R.id.image_recycler_view);
        addImgBtn = view.findViewById(R.id.add_img_btn);
        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the listener on AttachImageFragment
                attachImageFragment.setOnImageSelectedListener(InventoryAddEditFragment.this);
                Log.d("Attach image", "Going to fragment");
                NavigationManager.switchFragmentWithBack(attachImageFragment, getParentFragmentManager());
            }
        });

        displayedUris = attachImageFragment.getUriArrayList();

        adapter = new ImageRecyclerAdapter(displayedUris);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(activityContext, 2));
    }

    @Override
    public void onImageSelected(Uri imageUri) {
        // Handle the selected image URI here
        // You can add it to the display list or perform any other actions
        if (!displayedUris.contains(imageUri)) {
            Log.d("Attach image", "Selecting image");
            displayedUris.add(imageUri);
            adapter.updateDisplayedUris(displayedUris);  // Update the displayed images in the adapter
        }
    }



    /**
     * A method which sets up a listener to track whether the date field is pressed
     * @param view
     *      The view from which to find UI elements
     */
    public void setupDatePickerListener(View view){
        dateButton = view.findViewById(R.id.bio_editText);
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
            // clear all temporary pictures
            clearTempFiles();
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
                            selectedTags.add(tag);
                        }
                    }
                }
                String userID = googleAuthDB.getUid();
                Item new_item = new Item(name, selectedTags, date, val, make, model, serialNumber, desc, comment, userID);
                itemDB.addItemToDB(new_item);
                itemDB.addImagesToDB(displayedUris);
                // clear all temporary pictures
                clearTempFiles();
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

        String[] selectedTagNames = tagDropdown.getText().toString().split(", ");
        tagDB.getAllTags(new TagDB.OnGetTagsCallback() {

            @Override
            public void onSuccess(ArrayList<Tag> tagList) {

                for (String selectedTagName : selectedTagNames) {
                    for (Tag tag : tagList) {
                        if (tag.getName().equals(selectedTagName)){
                            selectedTags.add(tag);
                        }
                    }
                }
                String userID = googleAuthDB.getUid();
                Item new_item = new Item(name, selectedTags, date, val, make, model, serialNumber, desc, comment, userID);
                itemDB.updateItemInDB(item, new_item);
//                itemDB.updateImagesInDB(displayedUris); TODO
                // clear all temporary pictures
                clearTempFiles();
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
            tags = item.getTags();
        }
        if (selectedTags != null){
            ArrayList<String> selectedTagNames = new ArrayList<>();
            for (Tag tag : selectedTags) {
                selectedTagNames.add(tag.getName());
            }
            tagDropdown.setText(TextUtils.join(", ", selectedTagNames));
        }
    }

    private void showTagSelectionDialog() {
        TagDB tagDB = new TagDB();
        tagDB.getAllTags(new TagDB.OnGetTagsCallback() {
            @Override
            public void onSuccess(ArrayList<Tag> tagList) {
                boolean[] selectedTags = new boolean[tagList.size()];
                String[] tagNameList = new String[tagList.size()];

                // Reordering tags in the dialogue
                Comparator<Tag> tagComp = new Comparator<Tag>() {
                    @Override
                    public int compare(Tag tag1, Tag tag2) {
                        int result = tag1.getName().compareToIgnoreCase(tag2.getName());
                        return result;
                    }
                };
                tagList.sort(tagComp);

                // Creates the tag name list for the dialogue
                if (!tagList.isEmpty()) {
                    for (int i = 0; i < tagList.size(); i++) {
                        tagNameList[i] = tagList.get(i).getName();
                    }
                }

                // Checks what tags the item already has and checks it off in selectedTags
                if (!tags.isEmpty()) {
                    for (int i = 0; i < tagList.size(); i++) {
                        boolean itemHasTag = false;
                        for (Tag itemTag : tags) {
                            if (tagList.get(i).getDataBaseID().equals(itemTag.getDataBaseID())) {
                                itemHasTag = true;
                                break;
                            }
                        }
                        selectedTags[i] = itemHasTag;
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
                    // Adds the tags to the item
                    tags = new ArrayList<>();
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
                        tags = new ArrayList<>();
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

    /**
     * Clears temporary files from the directory obtained from the external files
     * directory in the Pictures directory.
     * Logs a message indicating the attempt to clear temporary files.
     * Deletes all files found in the directory.
     * Note: Ensure proper permissions to delete files from the directory.
     */

    private void clearTempFiles(){
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && storageDir.isDirectory()) {
            File[] files = storageDir.listFiles();
            Log.d("Temp files", "Clearing temp files");
            if (files != null) {
                for (File file : files) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        // Handle deletion failure if needed
                        // You can log or take appropriate action here
                    }
                }
            }
        }
    }
}
