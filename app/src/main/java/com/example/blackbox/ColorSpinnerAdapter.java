package com.example.blackbox;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ColorSpinnerAdapter extends ArrayAdapter<TagColor> {
    private Context context;
    private ArrayList<TagColor> colors;
    /**
     * Constructor for the ColorSpinnerAdapter.
     *
     * @param context The context of the application or activity.
     * @param tagColors  An ArrayList of color objects to be displayed in the dropdown view.
     */
    public ColorSpinnerAdapter(Context context, ArrayList<TagColor> tagColors){
        super(context, 0, tagColors);
        colors = tagColors;
        this.context = context;
    }

    /**
     * A method which populates the adapter with a default list of colors
     *
     * @return the list of tag colors
     */
    public ArrayList<TagColor> populateColors(Resources resources){

        colors.add(new TagColor("Red", resources.getColor(R.color.red, null)));
        colors.add(new TagColor("Orange", resources.getColor(R.color.orange, null)));
        colors.add(new TagColor("Yellow", resources.getColor(R.color.yellow, null)));
        colors.add(new TagColor("Green", resources.getColor(R.color.green, null)));
        colors.add(new TagColor("Blue", resources.getColor(R.color.blue, null)));
        colors.add(new TagColor("Purple", resources.getColor(R.color.purple, null)));

        return colors;
    }

    /**
     * A method which returns the list of TagColors
     * @return
     *      The list of tag colors
     */
    public ArrayList<TagColor> getColors(){
        return colors;
    }

    /**
     * A method which returns the index of a color in the adapters colors list
     * @param color
     *      The color to find in the list
     * @return
     *      The index of the provided color in the list
     */
    public int getColorIndex(String color){
        for (int i = 0; i < colors.size(); i++) {
            String element = colors.get(i).getName();
            if (element.equalsIgnoreCase(color)){
                return i;
            }
        }
        return -1;
    }
    /**
     * Get a view for a color that displays when the dropdown is closed
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
            view = LayoutInflater.from(context).inflate(R.layout.color_spinner, parent, false);
        }


        // get view elements
        TextView colorName = view.findViewById(R.id.color_name);
        ImageView colorView = view.findViewById(R.id.color_imageview);


        // set values for view elements
        TagColor color = colors.get(position);
        colorName.setText(color.getName());
        colorView.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));

        return view;
    }

    /**
     * Get a view that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view that the item view will be attached to.
     * @return The view for the item at the specified position.
     */
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;    // Allow recycling of old views
        Log.d("Color Test", "2" + colors.toString());

        // If view is null, inflate the item.xml layout
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.color_spinner, parent, false);
        }


        // get view elements
        TextView colorName = view.findViewById(R.id.color_name);
        ImageView colorView = view.findViewById(R.id.color_imageview);


        // set values for view elements
        TagColor color = colors.get(position);
        colorName.setText(color.getName());
        colorView.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));

        return view;
    }
}
