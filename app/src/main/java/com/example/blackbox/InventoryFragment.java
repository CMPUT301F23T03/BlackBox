package com.example.blackbox;

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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A Fragment that displays and manages an inventory of items. It includes features to add and edit items.
 */
public class InventoryFragment extends Fragment {
    ListView itemViewList;
    ArrayAdapter<Item> inventoryAdapter;
    ArrayList<Item> itemList;
    Button addButton;
    ListenerRegistration dbListener;
    private Context activityContext;
    InventoryDB inventoryDB;
    TagDB tagDB;
    InventoryEditFragment inventoryEditFragment = new InventoryEditFragment();
    InventoryAddFragment inventoryAddFragment = new InventoryAddFragment();
    private TextView totalSumTextView;
    // Add a member variable to store the total sum
    private double totalSum = 0.0;

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
     * @param view               The root view of the fragment.
     * @param savedInstanceState  A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize database
        inventoryDB = new InventoryDB();
        tagDB = new TagDB();

        // display the inventory list
        itemList = new ArrayList<>();
        itemViewList = (ListView) view.findViewById(R.id.item_list);
        inventoryAdapter = new InventoryListAdapter(activityContext, itemList);
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
                // THIS MAY BREAK THINGS ONCE SORTING IS IMPLEMENTED
                .orderBy("update_date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                // update inventory
                if (value != null){
                    handleGetInventory(value, e);
                }

            }
        });

        // add an item - display add fragment
        addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener((v) -> {
            NavigationManager.switchFragment(inventoryAddFragment, getParentFragmentManager());
        });

        // edit item - display edit fragment
        itemViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                inventoryEditFragment = InventoryEditFragment.newInstance(itemList.get(i));
                NavigationManager.switchFragment(inventoryEditFragment, getParentFragmentManager());
            }
        });

    }


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
                            sortByDate(ascending);
                            break;
                        case "By Value":
                            sortByValue(ascending);
                            break;
                        case "By Make":
                            sortByMake(ascending);
                            break;
                        case "By Tag":
                            sortByTag(ascending);
                            break;
                        default:
                            //handle the default case
                    }
                }
            }
        });

        builder.setView(mView);
        builder.create().show();
    }

    private void sortByDate(boolean ascending) {
        Comparator<Item> dateComp = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                //parse the date into appropriate format
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
                try{
                    Date date1 = dateFormat.parse(o1.getDateOfPurchase());
                    Date date2 = dateFormat.parse(o2.getDateOfPurchase());
                    if (date1 != null && date2 != null) {
                        return ascending ? date1.compareTo(date2) : date2.compareTo(date1);
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        itemList.sort(dateComp);
        inventoryAdapter.notifyDataSetChanged();
    }

    private void sortByValue(boolean ascending) {
        if (ascending){
            itemList.sort((item1, item2) -> Double.compare(item1.getEstimatedValue(), item2.getEstimatedValue()));
        }
        else {
            itemList.sort((item1, item2) -> Double.compare(item2.getEstimatedValue(), item1.getEstimatedValue()));
        }

        inventoryAdapter.notifyDataSetChanged();
    }

    private void sortByMake(boolean ascending) {
        Comparator<Item> makeComp = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                String make1 = o1.getMake();
                String make2 = o2.getMake();

                // compare alphabetically
                int result = make1.compareToIgnoreCase(make2);

                if (!ascending){
                    result = -result;
                }
                return result;
            }
        };
        itemList.sort(makeComp);
        inventoryAdapter.notifyDataSetChanged();
    }

    private void sortByTag(boolean ascending) {

    }

    /**
     * This method handles acquiring new data from the Firestore database
     * Uses a latch to ensure that it only returns after completing
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
            // Create a Tag object with the retrieved data

            Tag tag = new Tag(name, color, colorName, description);
            item.getTags().add(tag);
    }

    private double calculateTotalSum(ArrayList<Item> items) {
        double totalSum = 0.0;
        for (Item item : items) {
            totalSum += item.getEstimatedValue();
        }
        return totalSum;
    }

    public void updateTotalSum() {
        double totalSum = calculateTotalSum(itemList);
        totalSumTextView.setText("Total:" +StringFormatter.getMonetaryString(totalSum));
    }
}
