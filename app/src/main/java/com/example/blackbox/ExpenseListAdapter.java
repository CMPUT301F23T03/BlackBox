package com.example.blackbox;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.blackbox.R;
import com.example.blackbox.inventory.Item;
import com.example.blackbox.tag.Tag;
import com.example.blackbox.utils.StringFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * An adapter which displays expenses by tag
 */
public class ExpenseListAdapter extends ArrayAdapter {

    private ArrayList<Tag> tags;
    private ArrayList<Item> items;
    private Context context;

    /**
     * Constructor method for the ExpenseListAdapter
     * @param context
     *      The context of the view that the expense adapter is within
     * @param tags
     *      The list of tags to display
     * @param items
     *      The list of items which the tags are associated with
     */
    public ExpenseListAdapter(@NonNull Context context, ArrayList<Tag> tags, ArrayList<Item> items) {
        super(context, 0, tags);
        this.tags = tags;
        this.items = items;
        this.context = context;

    }

    /**
     * Creates a view for an individual expense
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     *      The created view
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_tag_list, parent, false);
        }

        TextView tagName = convertView.findViewById(R.id.expense_tag_name);
        TextView tagStatistic = convertView.findViewById(R.id.expense_tag_statistic);
        ImageView color = convertView.findViewById(R.id.color);

        Tag currentTag = tags.get(position);

        if (currentTag != null) {
            tagName.setText(currentTag.getName());
//            int backgroundColor = (position % 2 == 0) ? R.color.light_green : R.color.dark_green;
            color.setBackgroundTintList(ColorStateList.valueOf(currentTag.getColor()));
            double tagSum = calculateTagSum(currentTag);
            String val = StringFormatter.getMonetaryString(tagSum);
            tagStatistic.setText(val);
        }

        return convertView;
    }

    /**
     * Calculates the total value of all items with a given tag
     * @param tag
     *      The tag to calculate the total value of
     * @return
     *      A double representing the total value
     */
    private double calculateTagSum(Tag tag) {
        double tagSum = 0.0;
        for (Item item : items) {
            for (Tag itemTag : item.getTags()) {
                if (itemTag.getDataBaseID().equals(tag.getDataBaseID())) {
                    tagSum += item.getEstimatedValue();
                    break;
                }
            }
        }
        return tagSum;
    }

}