package com.example.blackbox.tag;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.blackbox.R;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter for populating an inventory list view with Item objects.
 */
public class TagAdapter extends ArrayAdapter {

    private ArrayList<Tag> tags;
    private Context context;

    /**
     * Constructor for the InventoryListAdapter.
     *
     * @param context The context of the application or activity.
     * @param tags   An ArrayList of Item objects to be displayed in the list view.
     */
    public TagAdapter(@NonNull Context context, ArrayList<Tag> tags) {
        super(context, 0, tags); // Call the constructor of the base class
        this.tags = tags;
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
            view = LayoutInflater.from(context).inflate(R.layout.tag_list, parent, false);
        }

        Tag tag = tags.get(position);   // Get the desired item in the list of items

        // Get the elements of the item
        TextView name = view.findViewById(R.id.name);
        ImageView colorBox = view.findViewById(R.id.color);
        TextView description = view.findViewById(R.id.description);

        // Set the text for the elements of the item
        name.setText(tag.getName());
        colorBox.setBackgroundTintList(ColorStateList.valueOf(tag.getColor()));
        description.setText(tag.getDescription());

        return view;
    }
}
