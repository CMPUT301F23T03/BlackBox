package com.example.blackbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class InventoryListAdapter extends ArrayAdapter {

        private ArrayList<Item> items;
        private Context context;

        // constructor
        public InventoryListAdapter(@NonNull Context context, ArrayList<Item> items) {
            super(context,0, items); // call constructor of the base class
            this.items = items;
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View view = convertView;    // allow recycle old views

            // if view is null, we inflate the all item.xml
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_list, parent,false);
            }

            Item item = items.get(position);   // get the wanted item in the list of items

            // get the elements of the item
            TextView name = view.findViewById(R.id.name);
            TextView value = view.findViewById(R.id.value);
            TextView desc = view.findViewById(R.id.desc);

            // change the texts of the element of the item
            name.setText(item.getName());
            String str_val = String.format("%.2f", item.getEstimatedValue()); // convert double to string with 2 decimal places
            value.setText("$" + str_val);
            desc.setText(item.getDescription());

            return view;
        }
}
