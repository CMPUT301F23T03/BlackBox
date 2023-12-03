package com.example.blackbox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.core.ui.LabelsFactory;
import com.example.blackbox.authentication.GoogleAuthDB;
import com.example.blackbox.inventory.InventoryDB;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.tag.TagDB;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.utils.NavigationManager;
import com.example.blackbox.utils.StringFormatter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.blackbox.profile.ProfileFragment;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A fragment class that displays summary information related to expenses and tags.
 */
public class ExpenseFragment extends Fragment{
    private ListView expenseTagListView;

    private Context activityContext;
    private InventoryDB inventoryDB;
    private ArrayList<Item> itemList;
    private ExpenseListAdapter expenseAdapter;
    private TextView totalExpenseTextView;
    private TextView info_text;
    private AnyChartView tagChartView;
    private TagDB tagDB;
    private ArrayList<Tag> tagList;
    private ListenerRegistration tagDBListener;
    private ListenerRegistration inventoryDBListener;
    private GoogleAuthDB googleAuthDB = new GoogleAuthDB();

    /**
     * Default constructor for the ExpenseFragment.
     */
    public ExpenseFragment(){}

    /**
     * Called to create the view for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).toggleBottomNavigationView(true);
        View view = inflater.inflate(R.layout.expense_fragment, container, false);

        // Display profile picture (taken from the Google account)
        ImageButton profilePicture = view.findViewById(R.id.profile_button);
        googleAuthDB.displayGoogleProfilePicture(profilePicture, 80, 80, this);

        return view;
    }

    /**
     * Called when the fragment's activity has been created.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize database
        inventoryDB = new InventoryDB();
        tagDB = new TagDB();

        tagList = new ArrayList<>();
        itemList = new ArrayList<>();
        expenseTagListView = view.findViewById(R.id.tag_expense_list);
        info_text = view.findViewById(R.id.info_text);
        totalExpenseTextView = view.findViewById(R.id.total_sum);
        tagChartView = view.findViewById(R.id.tagChartView);
        expenseAdapter = new ExpenseListAdapter(activityContext, tagList, itemList);
        expenseTagListView.setAdapter(expenseAdapter);
        expenseAdapter.notifyDataSetChanged();

        // Setting up DB listeners
        tagDBListener = tagDB.getTags()
                .whereEqualTo("user_id", googleAuthDB.getUid())
                .orderBy("update_date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // An error occurred while fetching the data
                            Log.e("Firestore", "Error getting inventory", e);
                            return;
                        }
                        tagList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String name = doc.getString("name");
                            int col = doc.getLong("color").intValue();
                            String desc = doc.getString("description");
                            String colorName = doc.getString("color_name");
                            String dbID = doc.getId();
                            String userID = doc.getString("user_id");
                            Tag tag = new Tag(name, col, colorName, desc, dbID, userID);
                            tag.setDateUpdatedWithString(doc.getString("update_date"));
                            // Only add tags that belong to the current user to the list to display
                            tagList.add(tag);
                        }
                        // Notify the adapter that the data has changed
                        featureVisibility();

                        expenseAdapter.notifyDataSetChanged();

                    }

                });


        inventoryDBListener = inventoryDB.getInventory()
                .whereEqualTo("user_id", googleAuthDB.getUid())
                // whenever database is update it is reordered by add date
                .orderBy("update_date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // An error occurred while fetching the data
                            Log.e("Firestore", "Error getting inventory", e);
                            return;
                        }
                        itemList.clear();
                        List<Task<DocumentSnapshot>> tagTasks = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            String name = doc.getString("name");
                            Double val = doc.getDouble("value");
                            String desc = doc.getString("description");
                            String make = doc.getString("make");
                            String model = doc.getString("model");
                            String serialNumber = doc.getString("serial_number");
                            String comment = doc.getString("comment");
                            String dateOfPurchase = doc.getString("purchase_date");
                            String dbID = doc.getId();
                            String userID = doc.getString("user_id");
                            ArrayList<Tag> tags = new ArrayList<>();
                            List<String> tagIDs = (List<String>) doc.get("tags");

                            Item item = new Item(name, tags, dateOfPurchase, val, make, model, serialNumber, desc, comment, dbID, userID);
                            if (tagIDs != null && !tagIDs.isEmpty()) {
                                for (String tagID : tagIDs) {
                                    for (Tag tag : tagList) {
                                        if (tagID.equals(tag.getDataBaseID())) {
                                            item.getTags().add(tag);
                                        }
                                    }
                                }
                            }
                            itemList.add(item);
                        }
                        // Total estimated value (same value show in inventory fragment)
                        updateTotalValue(itemList);

                        // Sort the tags by highest expense
                        sortTagsByExpense();

                        Pie pie = AnyChart.pie();

//                        List<Integer> colors = Arrays.asList(ContextCompat.getColor(getContext(), R.color.light_green),
//                                ContextCompat.getColor(getContext(), R.color.dark_green));

                        ArrayList<String> paletteColors = new ArrayList<>();

                        ArrayList<DataEntry> dataEntries = new ArrayList<>();
                        for (Tag tag : tagList) {
                            double pie_value = calculateTagSum(tag);
                            String tagName = tag.getName().replaceAll("'", "");;
                            // Use the color from the Tag object
                            String tagColor = tag.getHexStringColor();
                            paletteColors.add(tagColor);

                            dataEntries.add(new ValueDataEntry(tagName, pie_value));
                        }
                        // Convert ArrayList to array for the palette
                        String[] paletteArray = paletteColors.toArray(new String[0]);

                        // Set the palette for the pie chart
                        pie.palette(paletteArray);

                        pie.normal().stroke("#FFFFFF");
                        pie.hovered().stroke("#FFFFFF");

                        pie.hovered().stroke();
                        pie.labels(true);
                        pie.background().enabled(true);
                        pie.background().fill("#ffd54f 0.2");

                        // set labels position
                        LabelsFactory labels = pie.labels();
                        labels.position("center");
                        labels.anchor("center");
                        pie.labels().fontFamily("Menlo");
                        pie.labels().fontWeight(900);
                        pie.data(dataEntries);

                        tagChartView.setChart(pie);

                        // Notify the adapter that the data has changed
                        expenseAdapter.notifyDataSetChanged();
                    }
                });


        // When profile icon is clicked, switch to profile fragment
        ImageButton profileButton = view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                NavigationManager.switchFragmentWithoutBack(profileFragment, getParentFragmentManager());
            }
        });

    }

    /**
     * Helper function
     * Updates the visibility of features based on the presence of tags in the tagList.
     * If the tagList is empty, it sets the visibility of info_text and notifies the adapter.
     */
    private void featureVisibility() {
        if (tagList.isEmpty()) {
            info_text.setVisibility(View.VISIBLE);
            expenseAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Helper function
     * Updates the expense total value with the formatted monetary string.
     * @param items List of object Item belonging to user
     */
    private void updateTotalValue(List<Item> items) {
        double totalValue = calculateTotalValue(items);
        totalExpenseTextView.setText(StringFormatter.getMonetaryString(totalValue));
    }

    /**
     * Helper function
     * Calculates the total estimated value of a list of items.
     * @param items List of object Item belonging to user
     * @return Double totalValue
     */
    private double calculateTotalValue(List<Item> items) {
        double totalValue = 0.0;
        for (Item item : items) {
            totalValue += item.getEstimatedValue();
        }
        return totalValue;
    }

    /**
     * Sorts the tagList based on the expense (Descending Order)
     */
    private void sortTagsByExpense() {
        Comparator<Tag> tagComp = new Comparator<Tag>() {
            @Override
            public int compare(Tag tag1, Tag tag2) {
                double tag1Sum = calculateTagSum(tag1);
                double tag2Sum = calculateTagSum(tag2);
                return Double.compare(tag2Sum, tag1Sum);
            }
        };

        tagList.sort(tagComp);
    }

    /**
     * Calculates the sum of estimated values for a specific tag across all items in the itemList.
     * @param tag
     * @return Calculated sum of estimated values
     */
    private double calculateTagSum(Tag tag) {
        double tagSum = 0.0;
        for (Item item : itemList) {
            for (Tag itemTag : item.getTags()) {
                if (itemTag.getDataBaseID().equals(tag.getDataBaseID())) {
                    tagSum += item.getEstimatedValue();
                    break;
                }
            }
        }
        return tagSum;
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
     * Called when the fragment is detached from the activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        activityContext = null;
        tagDBListener.remove();
        inventoryDBListener.remove();
    }
}
