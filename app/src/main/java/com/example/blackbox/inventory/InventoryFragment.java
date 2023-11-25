package com.example.blackbox.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackbox.NavigationManager;
import com.example.blackbox.R;
import com.example.blackbox.StringFormatter;
import com.example.blackbox.inventory.filter.Filter;
import com.example.blackbox.inventory.filter.FilterDialog;
import com.example.blackbox.inventory.filter.FilterListAdapter;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.tag.TagDB;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A Fragment that displays and manages an inventory of items. It includes features to add and edit items.
 * This is the main fragment which the user will see when first starting the app.
 */
public class InventoryFragment extends Fragment {
    ListView itemViewList;

    private RecyclerView filterViewList;
    private SearchView searchView;
    private RecyclerView.Adapter filterAdapter;
    private ArrayList<Filter> filterList;
    ArrayAdapter<Item> inventoryAdapter;
    ItemList itemList;

    Button addButton;
    Button deleteButton;
    Button cancelButton;
    Button setTagButton;
    ListenerRegistration dbListener;
    private Button filterButton;
    private Context activityContext;
    InventoryDB inventoryDB;
    TagDB tagDB;
    InventoryEditFragment inventoryEditFragment = new InventoryEditFragment();
    InventoryAddFragment inventoryAddFragment = new InventoryAddFragment();
    private TextView totalSumTextView;
    // Add a member variable to store the total sum
    private double totalSum = 0.0;
    private ArrayList<Item> selectedItemsList = new ArrayList<>();
    private boolean isLongClick = false;

    /**
     * Default constructor for the InventoryFragment.
     */
    public InventoryFragment(){}

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
     * Called when the fragment is detached from the activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        activityContext = null;
        dbListener.remove();
    }


    /**
     * Called to create the view for the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     * @return The view for the fragment.
     */
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View ItemFragmentLayout = inflater.inflate(R.layout.inventory_fragment, container, false);
        return ItemFragmentLayout;
    }

    /**
     * Called when the fragment's view has been created.
     *
     * @param view                The root view of the fragment.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize database
        inventoryDB = new InventoryDB();
        tagDB = new TagDB();

        // display the inventory list
        itemList = new ItemList();
        filterList = new ArrayList<>();
        itemViewList = (ListView) view.findViewById(R.id.item_list);
        filterViewList = (RecyclerView) view.findViewById(R.id.filter_list);
        searchView = view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SearchView","Query submitted");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SearchView","Query changed");
                return false;
            }
        });

        inventoryAdapter = new InventoryListAdapter(activityContext, itemList);
        filterAdapter = new FilterListAdapter(filterList,itemList,inventoryAdapter, getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext,LinearLayoutManager.HORIZONTAL,false);
        //GridLayoutManager layoutManager = new GridLayoutManager(this,);

        filterViewList.setLayoutManager(layoutManager);
        filterViewList.setAdapter(filterAdapter);


        itemViewList.setAdapter(inventoryAdapter);
        totalSumTextView = view.findViewById(R.id.total_sum);

        // sort the inventory list
        Button buttonSort = view.findViewById(R.id.sort_button);
        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortOptionDialogue();
            }
        });


        // listener for data changes in DB
        dbListener =
                inventoryDB.getInventory()
                // whenever database is update it is reordered by add date
                .orderBy("update_date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                // update inventory
                if (value != null && !value.isEmpty()){
                    handleGetInventory(value, e);
                }
                else{
                    itemList.clear();
                    processUpdate();
                }

            }
        });

        filterButton = (Button) view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialog.showFilter(getActivity(),itemList,inventoryAdapter,filterAdapter);
                Log.d("FilterDialog","Returned from filter dialog");
                updateTotalSum();
            }
        });
        // add an item - display add fragment
        addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener((v) -> {
            NavigationManager.switchFragmentWithBack(inventoryAddFragment, getParentFragmentManager());
        });

        // edit item - display edit fragment
        itemViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isLongClick) {
                    // Regular click
                    inventoryEditFragment = InventoryEditFragment.newInstance(itemList.get(i));
                    NavigationManager.switchFragmentWithBack(inventoryEditFragment, getParentFragmentManager());
                } else {
                    toggleSelection(i);
                }
            }
        });

        deleteButton = view.findViewById(R.id.inventory_delete_button);
        cancelButton = view.findViewById(R.id.inventory_cancel_button);
        setTagButton = view.findViewById(R.id.set_tags_button);
        itemViewList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isLongClick) {
                    // Show a toast to indicate long click
                    Toast.makeText(requireContext(), "Multiselection Enabled", Toast.LENGTH_SHORT).show();

                    isLongClick = true;

                    // Toggle selection
                    toggleSelection(i);

                    // Hide the add button during long click
                    addButton.setVisibility(View.GONE);

                    // Make the delete and cancel button visible
                    deleteButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                    setTagButton.setVisibility(View.VISIBLE);


                }
                return true; // Return true to indicate that the long click event is consumed
            }
        });

        // CANCEL button on click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Item selectedItem: selectedItemsList){
                    selectedItem.setSelected(Boolean.FALSE);
                }

                exitMultiselect();

                // Update the view to reflect the change in selection
                inventoryAdapter.notifyDataSetChanged();
            }
        });

        // delete selected items on button click
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the selectedItemsList size is the same as the itemList size, clear the entire list
                if (selectedItemsList.size() == itemList.size()) {
                    // Clear the entire DB
                    inventoryDB.clearInventory();

                } else {
                    // Iterate through the selected items and delete them
                    for (Item selectedItem : selectedItemsList) {
                        // Delete item from Firestore or perform any other deletion logic
                        inventoryDB.deleteItem(selectedItem);
                        // Remove the item from the list
                        itemList.remove(selectedItem);
                    }
                }
                exitMultiselect();

                // Update the view to reflect the change in selection
                inventoryAdapter.notifyDataSetChanged();
            }
        });

        setTagButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (selectedItemsList.isEmpty()){
                      Toast.makeText(requireContext(), "No items have been selected.", Toast.LENGTH_SHORT).show();
                  } else {
                      showTagMultiSelectDialogue();
                  }

                  // Update the view to reflect the change in selection
                  inventoryAdapter.notifyDataSetChanged();
              }
        });
    }

    /**
     * Helper method to toggle whether an item in the list of items is currently selected
     * @param position
     *      The position of the item in the list
     */
    private void toggleSelection(int position) {
        // Toggle the selection state of the item
        Item selectedItem = itemList.get(position);
        selectedItem.setSelected(!selectedItem.isSelected());

        if (selectedItemsList.contains(selectedItem)) {
            // Item is already selected, remove it
            selectedItemsList.remove(selectedItem);
        } else {
            // Item is not selected, add it
            selectedItemsList.add(selectedItem);
        }

        // Update the view to reflect the change in selection
        inventoryAdapter.notifyDataSetChanged();
    }

    /**
     * Helper method to exit multi-selection
     */
    private void exitMultiselect(){
        // Clear the selection and update UI
        if (!selectedItemsList.isEmpty()) {
            selectedItemsList.clear();
        }

        // Hide the delete and cancel button
        deleteButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        setTagButton.setVisibility(View.GONE);

        // Show the add button
        addButton.setVisibility(View.VISIBLE);

        // Reset long click flag
        isLongClick = false;
    }

    /**
     * Shows a dialogue that allows the user to select multiple tags to assign to items
     */
    private void showTagMultiSelectDialogue() {
        TagDB tagDB = new TagDB();
        tagDB.getAllTags(new TagDB.OnGetTagsCallback() {
            @Override
            public void onSuccess(ArrayList<Tag> tagList) {
                boolean[] selectedTags = new boolean[tagList.size()];
                String[] tagNameList = new String[tagList.size()];

                // Sorting tag list multi-select dialogue
                Comparator<Tag> tagComp = new Comparator<Tag>() {
                    @Override
                    public int compare(Tag tag1, Tag tag2) {
                        int result = tag1.getName().compareToIgnoreCase(tag2.getName());
                        return result;
                    }
                };

                tagList.sort(tagComp);

                if (tagList.size() > 0) {
                    for (int i = 0; i < tagList.size(); i++) {
                        tagNameList[i] = tagList.get(i).getName();
                    }
                }

                // Check if items with the same tag are selected
                for (int i = 0; i < tagList.size(); i++) {
                    Tag currentTag = tagList.get(i);
                    selectedTags[i] = areAllSelectedItemsWithSameTag(currentTag);
                }
                boolean[] originalTags = selectedTags.clone();

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Set Tags");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(tagNameList, selectedTags, (dialogInterface, index, isChecked) -> {
                    // Update the selectedTags array when a tag is selected or deselected
                    selectedTags[index] = isChecked;
                });

                builder.setPositiveButton("OK", (dialogInterface, which) -> {

                    // Apply tags based on the conditions
                    for (Item selectedItem : selectedItemsList) {
                        // Tags that the selected item already has
                        ArrayList<Tag> itemsTags = selectedItem.getTags();

                        int tagIndex = 0;

                        for (Tag currentTag : tagList) {
                            boolean tagAlreadyExists = false;

                            // Check if the tag already exists in the item's tags
                            for (Tag itemTag : itemsTags) {
                                    if (itemTag.getDataBaseID().equals(currentTag.getDataBaseID())) {
                                        tagAlreadyExists = true;

                                        // If the selected tag for it is unchecked, that means the user wants to remove
                                        // the tag from all the selected items
                                        if (tagAlreadyExists && selectedTags[tagIndex] == false && originalTags[tagIndex] == true) {
                                            itemsTags.remove(itemTag);
                                        }
                                        break;
                                    }
                                }

                            if (selectedTags[tagIndex] && !tagAlreadyExists) {
                                // Add new tag if it doesn't already exist
                                itemsTags.add(currentTag);
                            }

                            tagIndex++;
                        }

                        // Recreate the item with updated tags
                        Item updatedItem = new Item(
                                selectedItem.getName(),
                                itemsTags,
                                selectedItem.getDateOfPurchase(),
                                selectedItem.getEstimatedValue(),
                                selectedItem.getMake(),
                                selectedItem.getModel(),
                                selectedItem.getSerialNumber(),
                                selectedItem.getDescription(),
                                selectedItem.getComment()
                        );

                        // Update the item in the database
                        inventoryDB.updateItemInDB(selectedItem, updatedItem);

                    }
                    // Refresh selectedItemsList after updating items
                    selectedItemsList.clear();
//                    selectedItemsList.addAll(updatedItemsListFromDatabase);
                    inventoryAdapter.notifyDataSetChanged();
                });

                builder.setNegativeButton("Cancel", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
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
     * Helper method to determine where the selected items have the common tag
     * @param tag
     * @return true if all items have the tag; false otherwise
     */
    private boolean areAllSelectedItemsWithSameTag(Tag tag) {
        // Check if all selected items have the same tag based on name comparison
        for (Item selectedItem : selectedItemsList) {
            boolean hasTag = false;

            // Perform name comparison for each tag in the item's tag list
            for (Tag itemTag : selectedItem.getTags()) {
                if (itemTag.getName().equals(tag.getName())) {
                    hasTag = true;
                    break;
                }
            }

            // If the item does not have the tag, return false
            if (!hasTag) {
                return false;
            }
        }

        // All selected items have the same tag based on name comparison
        return true;
    }

    /**
     * A dialogue box that shows two spinners which lets the user choose the sorting category and sorting order
     * and calls the appropriate sort function based on the selection.
     */
    private void showSortOptionDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View mView = getLayoutInflater().inflate(R.layout.custom_sort_spinner, null);

        builder.setTitle("Sorting Options");
        builder.setCancelable(false);

        Spinner category_spinner = mView.findViewById(R.id.sort_category_spinner);
        Spinner order_spinner = mView.findViewById(R.id.sort_order_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.sort_category));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.sort_order));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order_spinner.setAdapter(adapter1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedCategory = category_spinner.getSelectedItem().toString();
                String selectedOrder = order_spinner.getSelectedItem().toString();
                boolean ascending = selectedOrder.equalsIgnoreCase("Ascending");

                if(!selectedCategory.equalsIgnoreCase("Sorting Category") && !selectedOrder.equalsIgnoreCase("Sorting Order")){
                    //handle the selected sorting category
                    switch (selectedCategory){
                        case "By Date":
                            itemList.sortByDate(ascending);
                            break;
                        case "By Value":
                            itemList.sortByValue(ascending);
                            break;
                        case "By Make":
                            itemList.sortByMake(ascending);
                            break;
                        case "By Tag":
                            itemList.sortByHighestPrecedentTag(ascending);
                            break;
                        default:
                            //handle the default case
                    }
                    inventoryAdapter.notifyDataSetChanged();
                }

            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, which) -> {
           dialogInterface.dismiss();
        });

        builder.setView(mView);
        builder.create().show();
    }

    /**
     * This method handles acquiring new data from the Firestore database
     * and ensures that all async firestore tasks complete before processing the update
     * all async firestore tasks
     * @param snapshot
     *      The querySnapshot to process
     * @param e
     *      A possible Firestore exception
     */
    private void handleGetInventory(QuerySnapshot snapshot, FirebaseFirestoreException e){
        if (e != null) {
            // Handle any errors or exceptions
            return;
        }
        itemList.clear();
        List<Task<DocumentSnapshot>> tagTasks = new ArrayList<>();
        for (QueryDocumentSnapshot doc : snapshot) {
            String name = doc.getString("name");
            Double val = doc.getDouble("value");
            String desc = doc.getString("description");
            String make = doc.getString("make");
            String model = doc.getString("model");
            String serialNumber = doc.getString("serial_number");
            String comment = doc.getString("comment");
            String dateOfPurchase = doc.getString("purchase_date");
            String dbID = doc.getId();
            ArrayList<Tag> tags = new ArrayList<>();

            List<String> tagIDs = (List<String>) doc.get("tags");

            Item item = new Item(name, tags, dateOfPurchase, val, make, model, serialNumber, desc, comment, dbID);
            if (tagIDs != null && !tagIDs.isEmpty()) {
                for (String tagID : tagIDs) {
                    Task<DocumentSnapshot> tagTask = tagDB.getTags().document(tagID).get();
                    tagTasks.add(tagTask);
                    Log.d("Firestore", "added task");
                    tagTask.addOnSuccessListener(tagSnapshot -> {
                        fetchTagForItem(item, tagSnapshot);
                    });
                }
            }
            itemList.add(item);

        }
        if (tagTasks.size() > 0){
            Tasks.whenAll(tagTasks).addOnCompleteListener(task -> {
                Log.d("Firestore", "All tag tasks done");
                processUpdate();
            });
        }
        else{
            Log.d("Firestore", "All tag tasks done");
            processUpdate();
        }
    }


    /**
     * Make updates after retrieving all data from the database
     */
    private void processUpdate(){
        // preform updates
        inventoryAdapter.notifyDataSetChanged();
        updateTotalSum();
        Log.d("Firestore", "Processed Update");
    }

    /**
     * Fetches tags associated with an item from the Firestore database and
     * populates the item's tag list.
     *
     * @param item
     *      The item to update
     * @param document
     *      The document to retrieve the tag
     */
    private void fetchTagForItem(Item item,  DocumentSnapshot document) {
        // Access the db instance from InventoryDB
            String name = document.getString("name");
            int color = document.getLong("color").intValue();
            String colorName = document.getString("colorName");
            String description = document.getString("description");
            String dataBaseId = document.getId();

            // Create a Tag object with the retrieved data
            Tag tag = new Tag(name, color, colorName, description, dataBaseId);
            item.getTags().add(tag);
    }

    /**
     * Get the total sum of all items estimated values and display it to the screen
     */
    public void updateTotalSum() {
        Double totalSum = itemList.calculateTotalSum();
        totalSumTextView.setText("Total: " + StringFormatter.getMonetaryString(totalSum));
    }
}