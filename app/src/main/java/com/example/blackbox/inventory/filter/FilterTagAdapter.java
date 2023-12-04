package com.example.blackbox.inventory.filter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackbox.R;
import com.example.blackbox.tag.Tag;

import java.util.ArrayList;

public class FilterTagAdapter extends RecyclerView.Adapter<FilterTagAdapter.ViewHolder> {
    private ArrayList<Tag> taglist;

    private ArrayList<Tag> selectedTags;
    private LinearLayoutManager layoutManager;

    public FilterTagAdapter(ArrayList<Tag> tagList,LinearLayoutManager layoutManager){
        selectedTags = new ArrayList<>();
        this.taglist = tagList;
        this.layoutManager = layoutManager;
    }

    @Override
    public int getItemCount(){
        return taglist.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int position){
        View newView = LayoutInflater.from(group.getContext()).inflate(R.layout.filter_tag,group,false);
        return new ViewHolder(newView);
    }

    public ArrayList<Tag> getSelectedTags(){
        return selectedTags;
    }

    public void shadeSelectedTags(){
        for (Tag selectedTag : selectedTags){
            View selectedView = layoutManager.findViewByPosition(taglist.indexOf(selectedTag));
            selectedView.setBackgroundColor(Color.LTGRAY);
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int position){
        viewHolder.getName().setText(taglist.get(viewHolder.getAdapterPosition()).getName());
        viewHolder.getImage().setBackgroundTintList(ColorStateList.valueOf(taglist.get(viewHolder.getAdapterPosition()).getColor()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedTags.contains(taglist.get(viewHolder.getLayoutPosition()))){
                    selectedTags.remove(taglist.get(viewHolder.getLayoutPosition()));
                    v.setBackgroundColor(Color.TRANSPARENT);

                }else{
                    selectedTags.add(taglist.get(viewHolder.getLayoutPosition()));
                    v.setBackgroundColor(Color.LTGRAY);
                }

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        public ViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.filter_tag_color);
            name = view.findViewById(R.id.filter_tag_name);
        }

        public TextView getName(){
            return name;
        }

        public ImageView getImage(){
            return image;
        }
    }
}
