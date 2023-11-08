package com.example.blackbox;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
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
        ImageView tagImage2 = view.findViewById(R.id.tag_image2);


        // Set the text for the elements of the item
        name.setText(item.getName());
        String str_val = StringFormatter.getMonetaryString(item.getEstimatedValue()); // Convert double to a string with 2 decimal places
        value.setText(str_val);

        String strDesc = item.getDescription();
        int maxLength = 70;
        // Check if the description is too long and shorten if required
        if (strDesc.length() > maxLength) {
            strDesc = strDesc.substring(0, maxLength - 1) + "...";
        }

        desc.setText(strDesc);

        if (item.getNumTags() == 0 ){
            // Hide the tag images
            tagImage.setVisibility(View.GONE);
            tagImage2.setVisibility(View.GONE);
            Log.d("SetColor", "Not Tags " + item.getName());

        }
        else if (item.getNumTags() == 1) {
            // hide one tag image
            tagImage.setVisibility(View.VISIBLE);
            tagImage2.setVisibility(View.GONE);
            Log.d("SetColor", "One Tag " + item.getName());
            tagImage.setBackgroundTintList(ColorStateList.valueOf(item.getTags().get(0).getColor()));
        }
        else{
            // show both tag images
            tagImage.setVisibility(View.VISIBLE);
            tagImage2.setVisibility(View.VISIBLE);
            // this has to be set otherwise it will default to something weird sometimes
            tagImage.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            tagImage2.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

        }

        return view;
    }
}
