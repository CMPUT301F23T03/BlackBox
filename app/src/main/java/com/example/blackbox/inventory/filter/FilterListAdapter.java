package com.example.blackbox.inventory.filter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackbox.R;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.utils.StringFormatter;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.inventory.ItemList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {
    private ArrayList<Filter> filterList;
    private ItemList itemList;

    private ArrayAdapter<Item> inventoryAdapter;
    private FragmentActivity activity;
    private TextView totalSum;


    /**
     * Initializes all of the relevant information need for the adapter.
     * @param filterList to store list of applied filters
     * @param itemList to store the list of items displayed in InventoryFragment
     * @param inventoryAdapter to be able to put into effects changes made to itemList
     * @param activity to access the totalSum displayed in InventoryFragment
     * @see com.example.blackbox.inventory.InventoryFragment
     */
    public FilterListAdapter(ArrayList<Filter> filterList, ItemList itemList, ArrayAdapter<Item> inventoryAdapter, FragmentActivity activity) {
        this.filterList = filterList;
        this.totalSum = activity.findViewById(R.id.total_sum);
        this.activity = activity;
        this.itemList = itemList;
        this.inventoryAdapter = inventoryAdapter;
    }


    /**
     * Creates a new View for a given filter, and returns the ViewHolder for that View
     * @param group The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param position The view type of the new View.
     *
     * @return ViewHolder
     *
     * @see androidx.recyclerview.widget.RecyclerView.ViewHolder
     * @see View
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int position) {
        View newView = LayoutInflater.from(group.getContext()).inflate(R.layout.filter, group, false);
        return new ViewHolder(newView);
    }

    /**
     * Updates the total sum shown in the InventoryFragment
     * @see com.example.blackbox.inventory.InventoryFragment
     */
    private void updateTotalSum() {
        Double totalSum = itemList.calculateTotalSum();
        this.totalSum.setText("Total: " + StringFormatter.getMonetaryString(totalSum));

    }


    /**
     * Initializes the information for a view once it is bound to a specific filter<br><br>
     * Also creates the onClickListener for that view.
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(filterList.get(viewHolder.getAdapterPosition()).getFilterName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemList(viewHolder.getLayoutPosition());
                updateTotalSum();
                filterList.remove(viewHolder.getLayoutPosition());
                FilterListAdapter.this.notifyDataSetChanged();
            }
        });
    }

    /**
     * Clears the ArrayList <Filter> filterList by adding all of the items from each filter back into <br>
     * ArrayList<Item> itemList and then calling updateTotalSum() to update the sum displayed.
     */
    public void clearFilters() {
        for (Filter filter : filterList) {
            for (Item item : filter.getItemList()) {
                itemList.add(item);
            }
        }
        updateTotalSum();
        filterList.clear();
        inventoryAdapter.notifyDataSetChanged();
        FilterListAdapter.this.notifyDataSetChanged();
    }

    /**
     * Return size of ArrayList<Filter> filterList.
     * @return int
     */
    @Override
    public int getItemCount() {
        return filterList.size();
    }

    /**
     * Checks the items of the filter to be deleted and updates the itemList with each item if
     * that matches all other filters
     * <br><br>If not, adds corresponding item to Filter that filtered out the item.
     * @param filterPosition used to get the the items for the filter at the specified location
     */
    private void updateItemList(int filterPosition) {
        Filter clickedFilter = filterList.get(filterPosition);
        ArrayList<Item> filterItemList = clickedFilter.getItemList();
        if (filterList.size() == 1) {
            for (Item filteredItem : filterItemList) {
                itemList.add(filteredItem);
            }
        } else {
            for (Item filteredItem : filterItemList) {
                boolean itemFiltered = false;
                for (Filter filter : filterList) {
                    if (filterList.indexOf(filter) != filterPosition) {
                        switch (filter.getFilterType()) {
                            case "make":
                                if (!filteredItem.getMake().equalsIgnoreCase(filter.getMake())) {
                                    filter.addItemToFilter(filteredItem);
                                    itemFiltered = true;
                                }
                                break;
                            case "date":
                                String lowerDate = filter.getDateRange()[0];
                                String upperDate = filter.getDateRange()[1];
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    Date filterLowerDate = dateFormat.parse(lowerDate);
                                    Date filterUpperDate = dateFormat.parse(upperDate);
                                    Date date = dateFormat.parse(filteredItem.getDateOfPurchase());
                                    if (!(date.equals(filterLowerDate) || date.equals(filterUpperDate))) {
                                        if (date.after(filterUpperDate) || date.before(filterLowerDate)) {
                                            filter.addItemToFilter(filteredItem);
                                            itemFiltered = true;
                                        }
                                    }
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                            case "price":
                                double lowerRange = filter.getPriceRange()[0];
                                double upperRange = filter.getPriceRange()[1];
                                double cost = filteredItem.getEstimatedValue();
                                if (!((cost == lowerRange || cost == upperRange) || (cost < upperRange && cost > lowerRange))) {
                                    filter.addItemToFilter(filteredItem);
                                    itemFiltered = true;
                                }
                                break;
                            case "tag":
                                if (!filterByTags(filter, filteredItem)) {
                                    filter.addItemToFilter(filteredItem);
                                    itemFiltered = true;
                                }
                                break;
                            case "keyword":
                                if (!filterBySearch(filter.getKeywords(), filteredItem)) {
                                    filter.addItemToFilter(filteredItem);
                                    itemFiltered = true;
                                }
                                break;
                        }

                    }

                }
                if (!itemFiltered) {
//                            if (!itemList.contains(filteredItem)) {
                    this.itemList.add(filteredItem);
//                            }
                }
            }
        }
        inventoryAdapter.notifyDataSetChanged();
    }

    /**
     * Returns ArrayList<Filter>, holding all Filters.
     * @return ArrayList<Filter>
     */
    public ArrayList<Filter> getFilterList() {
        return this.filterList;
    }

    /**
     * Adds given filter to ArrayList<Filter>, holding all filters.
     * @param filter added to ArrayList<Filter> of Filters.
     */
    public void addItem(Filter filter) {
        filterList.add(filter);
    }

    /**
     * Class that generates ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView filter;

        /**
         * Used to initialize ViewHolder object.
         * @param view passed into super()
         */
        public ViewHolder(View view) {
            super(view);
            filter = (TextView) view.findViewById(R.id.filter_name);
        }

        /**
         * Returns the TextView that stores the name of the filter
         * @return TextView
         */
        public TextView getTextView() {

            return filter;
        }
    }

    /**
     * Function that creates and returns a Set<String> for the given ArrayList<Tag>
     * @param tags
     * @return Set<String>
     * @see Tag
     */
    private Set<String> createSet(ArrayList<Tag> tags) {
        Set<String> returnSet = new HashSet<>();

        for (Tag tag : tags) {
            returnSet.add(tag.getDataBaseID());
        }

        return returnSet;
    }

    /**
     * Creates two sets, and checks if the intersection of them is of size greater than the given query, stored in tokenArray
     * @param tokenArray  used to create set
     * @param item  used to check if item matches items in tokenArray
     * @return
     * @see Set
     */
    private boolean filterBySearch(String[] tokenArray, Item item) {
        Set<String> keywordTokens = createSet(tokenArray);
        Set<String> itemDescription = createSet(item.getDescription().toLowerCase().split(" "));
        Set<String> intersectionSet = new HashSet<>(itemDescription);
        intersectionSet.retainAll(keywordTokens);
        if (intersectionSet.size() < tokenArray.length) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Function that creates and returns a Set<String> for the given String[]
     * @param tokens
     * @return Set<String>
     */
    private Set<String> createSet(String[] tokens) {
        Set<String> returnSet = new HashSet<>();

        for (String token : tokens) {
            returnSet.add(token);
        }

        return returnSet;
    }

    /**
     * Creates two sets, and checks if the intersection of them is of size greater than the given query, stored in the ArrayList of Tags for the Filter.
     * @param filter used to generate set of type Tags
     * @param item used to check if item matches tags set in Filter
     * @return
     * @see Set
     */
    private boolean filterByTags(Filter filter, Item item) {
        ArrayList<Tag> selectedTags = filter.getTagArrayList();
        Set<String> selectedTagsSet = createSet(selectedTags);
        Set<String> itemSet = createSet(item.getTags());
        Set<String> intersectionSet = new HashSet<>(itemSet);
        intersectionSet.retainAll(selectedTagsSet);
        if (intersectionSet.size() < selectedTags.size()) {
            return false;
        } else {
            return true;
        }

    }
}
