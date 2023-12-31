package com.example.blackbox.inventory;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.checkerframework.checker.units.qual.A;

import com.example.blackbox.R;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.utils.StringFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A custom ArrayAdapter for populating an inventory list view with Item objects.
 */
public class InventoryListAdapter extends ArrayAdapter implements Filterable {

    private ArrayList<Item> originalItems;

    private ArrayList<Item> items;
    private Context context;

    private String notifyReason = "";

    /**
     * Constructor for the InventoryListAdapter.
     *
     * @param context The context of the application or activity.
     * @param items   An ArrayList of Item objects to be displayed in the list view.
     */
    public InventoryListAdapter(@NonNull Context context, ArrayList<Item> items) {
        super(context, 0, items); // Call the constructor of the base class
        this.items = items;
        this.originalItems = null;
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
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView name = view.findViewById(R.id.name);
        TextView value = view.findViewById(R.id.value);
        TextView desc = view.findViewById(R.id.desc);
        ImageView tagImage = view.findViewById(R.id.tag_image);
        ImageView tagImage2 = view.findViewById(R.id.tag_image2);

        // set image
        if (item.getDisplayImageUri() != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(item.getDisplayImageUri());
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }


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

        if (item.getNumTags() == 0) {
            // Hide the tag images
            tagImage.setVisibility(View.GONE);
            tagImage2.setVisibility(View.GONE);
            Log.d("SetColor", "Not Tags " + item.getName());

        } else if (item.getNumTags() == 1) {
            // hide one tag image
            tagImage.setVisibility(View.VISIBLE);
            tagImage2.setVisibility(View.GONE);
            Log.d("SetColor", "One Tag " + item.getName());
            tagImage.setBackgroundTintList(ColorStateList.valueOf(item.getTags().get(0).getColor()));
        } else {
            // show both tag images
            tagImage.setVisibility(View.VISIBLE);
            tagImage2.setVisibility(View.VISIBLE);
            int currentNightMode = context.getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            // set color based on whether in dark or light mode
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                tagImage.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                tagImage2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            } else {
                // this has to be set otherwise it will default to something weird sometimes
                tagImage.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                tagImage2.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            }


        }

        // Highlights the item to show it's selected in multi-selection
        int backgroundColor = item.isSelected() ?
                ContextCompat.getColor(context, android.R.color.holo_blue_light) :
                Color.TRANSPARENT;
        Log.d("BackgroundColor", "Item: " + item.getName() + ", isSelected: " + item.isSelected() + ", Color: " + backgroundColor);
        view.findViewById(R.id.inventory_list_item).setBackgroundColor(backgroundColor);

        return view;
    }

    private Set<String> createSet(String[] tokens) {
        Set<String> returnSet = new HashSet<>();

        for (String token : tokens) {
            returnSet.add(token);
        }

        return returnSet;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("InventoryListAdapter", "filtering reached");
                FilterResults filterResults = new FilterResults();


                if (notifyReason.equalsIgnoreCase("")) {
                    originalItems = (ArrayList<Item>) items.clone();
                }
                if (constraint != null) {
                    String[] filterTokens = constraint.toString().toLowerCase().split(",");
                    Set<String> filterTokensSet = createSet(filterTokens);
                    ArrayList<Item> results = new ArrayList<>();
                    for (Item item : originalItems) {
                        Set<String> itemDescription = createSet(item.getDescription().toLowerCase().split(" "));
                        Set<String> itemName = createSet(item.getName().toLowerCase().split(" "));
                        Set<String> nameIntersectionSet = new HashSet<>(filterTokensSet);
                        Set<String> intersectionSet = new HashSet<>(filterTokensSet);
                        nameIntersectionSet.retainAll(itemName);
                        intersectionSet.retainAll(itemDescription);
                        if (intersectionSet.size() >= filterTokens.length || nameIntersectionSet.size() >= filterTokens.length){
                            results.add(item);
                        }
                    }
                    filterResults.values = results;
                    filterResults.count = results.size();
                } else {
                    //count here is kept to 0 so that we know that the results returned nothing
                    filterResults.values = originalItems;
                    filterResults.count = 0;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    Log.d("InventoryListAdapter", "Data set notified");
                    Toast.makeText(context, results.count + " Result(s) Found", Toast.LENGTH_SHORT).show();
                    items.clear();
                    items.addAll((Collection<? extends Item>) results.values);
                    notifyReason = "filter";
                    notifyDataSetChanged();
                } else if (constraint != null) {
                    items.clear();
                    notifyReason = "filter";
                    notifyDataSetChanged();
                    Toast.makeText(context, "No Results Found", Toast.LENGTH_SHORT).show();
                } else {
                    items.clear();
                    items.addAll((Collection<? extends Item>) results.values);
                    Log.d("InventoryListAdapter", "items restored" + items.toString());
                    notifyReason = "";
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

}


