package com.example.blackbox.inventory.filter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackbox.R;
import com.example.blackbox.utils.StringFormatter;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.inventory.ItemList;

import java.util.ArrayList;

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
            updateItemList(filterList.indexOf(filter));
            updateTotalSum();
            filterList.remove(filter);
        }
        FilterListAdapter.this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return filterList.size();
    }

    private void updateItemList(int filterPosition){
        Filter clickedFilter = filterList.get(filterPosition);
        ArrayList<Item> filterItemList = clickedFilter.getItemList();
        for (Item filteredItem :filterItemList){
            this.itemList.add(filteredItem);
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
}
