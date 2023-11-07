package com.example.blackbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter for populating an inventory list view with Item objects.
 */
public class InventoryListAdapter extends ArrayAdapter {

    private ArrayList<Item> items;
    private Context context;

    /**
     * Constructor for the InventoryListAdapter.
     *
     * @param context The context of the application or activity.
     * @param items   An ArrayList of Item objects to be displayed in the list view.
     */
    public InventoryListAdapter(@NonNull Context context, ArrayList<Item> items) {
        super(context, 0, items); // Call the constructor of the base class
        this.items = items;
        this.context = context;
    }

    /**
     * Get a view that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view that the item view will be attached to.
     * @return The view for the item at the specified position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;    // Allow recycling of old views

        // If view is null, inflate the item.xml layout
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        }


        Item item = items.get(position);   // Get the desired item in the list of items

        // Get the elements of the item
        TextView name = view.findViewById(R.id.name);
        TextView value = view.findViewById(R.id.value);
        TextView desc = view.findViewById(R.id.desc);
        ImageView tagImage = view.findViewById(R.id.tag_image);

        // Set the text for the elements of the item
        name.setText(item.getName());
        String str_val = String.format("%.2f", item.getEstimatedValue()); // Convert double to a string with 2 decimal places
        value.setText("$" + str_val);
        desc.setText(item.getDescription());

        if (item.hasTags()) {
            tagImage.setVisibility(View.VISIBLE); // Show the tag image
        } else {
            tagImage.setVisibility(View.GONE); // Hide the tag image
        }

        return view;
    }
}
