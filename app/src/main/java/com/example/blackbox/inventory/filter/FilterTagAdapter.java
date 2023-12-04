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

    /**
     * Used to initialize list of selectedTags, which holds all of the tags that have clicked.
     * @param tagList stores all of the tags available to be acted upon.
     * @param layoutManager references the layoutManager used for the Adapter
     */
    public FilterTagAdapter(ArrayList<Tag> tagList,LinearLayoutManager layoutManager){
        selectedTags = new ArrayList<>();
        this.taglist = tagList;
        this.layoutManager = layoutManager;
    }

    /**
     * Returns size of ArrayList that holds all of the tags
     * @return int
     */
    @Override
    public int getItemCount(){
        return taglist.size();
    }
    /**
     * Creates a new View for a given Tag, and returns the ViewHolder for that View
     * @param group The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param position The view type of the new View.
     *
     * @return ViewHolder
     *
     * @see androidx.recyclerview.widget.RecyclerView.ViewHolder
     * @see View
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int position){
        View newView = LayoutInflater.from(group.getContext()).inflate(R.layout.filter_tag,group,false);
        return new ViewHolder(newView);
    }

    /**
     * Returns the arrayList of selected Tags
     * @return ArrayList of Tags
     */
    public ArrayList<Tag> getSelectedTags(){
        return selectedTags;
    }

    /**
     * Used to change the background of the selected tags to Light Gray
     */
    public void shadeSelectedTags(){
        for (Tag selectedTag : selectedTags){
            View selectedView = layoutManager.findViewByPosition(taglist.indexOf(selectedTag));
            selectedView.setBackgroundColor(Color.LTGRAY);
        }
    }


    /**
     * Initializes the information for a view once it is bound to a specific Tag<br><br>
     * Also creates the onClickListener for that view.
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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

    /**
     * Class that generates ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        /**
         * Used to initialize ViewHolder object.
         * @param view passed into super()
         */
        public ViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.filter_tag_color);
            name = view.findViewById(R.id.filter_tag_name);
        }

        /**
         * Returns TextView that holds the name of the tag.
         * @return TextView
         */
        public TextView getName(){
            return name;
        }

        /**
         * Returns the ImageView that holds the corresponding color for the tag.
         * @return ImageView
         */
        public ImageView getImage(){
            return image;
        }
    }
}
