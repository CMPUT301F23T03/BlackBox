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

public class ExpenseListAdapter extends ArrayAdapter {

    private ArrayList<Tag> tags;
    private ArrayList<Item> items;
    private Context context;

    public ExpenseListAdapter(@NonNull Context context, ArrayList<Tag> tags, ArrayList<Item> items) {
        super(context, 0, tags);
        this.tags = tags;
        this.items = items;
        this.context = context;

    }

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