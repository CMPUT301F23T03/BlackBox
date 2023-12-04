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



    public FilterListAdapter(ArrayList<Filter> filterList, ItemList itemList, ArrayAdapter<Item> inventoryAdapter, FragmentActivity activity) {
        this.filterList = filterList;
        this.totalSum = activity.findViewById(R.id.total_sum);
        this.activity = activity;
        this.itemList = itemList;
        this.inventoryAdapter = inventoryAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int position){
        View newView = LayoutInflater.from(group.getContext()).inflate(R.layout.filter,group,false);
        return new ViewHolder(newView);
    }

    private void updateTotalSum(){
        Double totalSum = itemList.calculateTotalSum();
        this.totalSum.setText("Total: " + StringFormatter.getMonetaryString(totalSum));

    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position){
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

    public void clearFilters(){
        for (Filter filter: filterList){
            for (Item item: filter.getItemList()){
                itemList.add(item);
            }
        }
        updateTotalSum();
        filterList.clear();
        inventoryAdapter.notifyDataSetChanged();
        FilterListAdapter.this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return filterList.size();
    }

    private void updateItemList(int filterPosition){
        Filter clickedFilter = filterList.get(filterPosition);
        ArrayList<Item> filterItemList = clickedFilter.getItemList();
        if (filterList.size() == 1){
            for (Item filteredItem : filterItemList){
                itemList.add(filteredItem);
            }
        }else {
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
                                if (!filterByTags(filter,filteredItem)){
                                    filter.addItemToFilter(filteredItem);
                                    itemFiltered = true;
                                }
                                break;
                        }
                        if (!itemFiltered) {
                            this.itemList.add(filteredItem);
                        }
                    }
                }
            }
        }
        inventoryAdapter.notifyDataSetChanged();
    }

    public ArrayList<Filter> getFilterList(){
        return this.filterList;
    }

    public void addItem(Filter filter){
        filterList.add(filter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView filter;
        public ViewHolder(View view){
            super(view);
            filter = (TextView) view.findViewById(R.id.filter_name);
        }

        public TextView getTextView(){

            return filter;
        }
    }


    private Set<String> createSet (ArrayList<Tag> tags){
        Set<String> returnSet = new HashSet<>();

        for (Tag tag : tags){
            returnSet.add(tag.getDataBaseID());
        }

        return returnSet;
    }
    private boolean filterByTags(Filter filter, Item item){
        ArrayList<Tag> selectedTags = filter.getTagArrayList();
        Set<String> selectedTagsSet = createSet(selectedTags);
        Set<String> itemSet = createSet(item.getTags());
        Set<String> intersectionSet = new HashSet<>(itemSet);
        intersectionSet.retainAll(selectedTagsSet);
        if (intersectionSet.size() < selectedTags.size()){
            return false;
        }else{
            return true;
        }

    }
}
