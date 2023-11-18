package com.example.blackbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {
    private ArrayList<Filter> filterList;
    private ArrayList<Item> itemList;
    private ArrayAdapter<Item> inventoryAdapter;

    public FilterListAdapter(ArrayList<Filter> filterList,ArrayList<Item> itemList, ArrayAdapter<Item> inventoryAdapter) {
        this.filterList = filterList;
        this.itemList = itemList;
        this.inventoryAdapter = inventoryAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int position){
        View newView = LayoutInflater.from(group.getContext()).inflate(R.layout.filter,group,false);
        return new ViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position){
        viewHolder.getTextView().setText(filterList.get(viewHolder.getAdapterPosition()).getFilterType());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemList(viewHolder.getLayoutPosition());
                filterList.remove(viewHolder.getLayoutPosition());
                FilterListAdapter.this.notifyDataSetChanged();
            }
        });
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

    public void addItem(Filter filter){
        filterList.add(filter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView filter;
        public ViewHolder(View view){
            super(view);
            filter = (TextView) view.findViewById(R.id.filter);
        }

        public TextView getTextView(){

            return filter;
        }
    }
}
