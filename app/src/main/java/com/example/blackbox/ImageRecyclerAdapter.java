package com.example.blackbox;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder> {
    private ArrayList<Uri> displayedUris;  // Track displayed images

    public ImageRecyclerAdapter(ArrayList<Uri> displayedUris) {
        this.displayedUris = displayedUris;
    }

    @NonNull
    @Override
    public ImageRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecyclerAdapter.ViewHolder holder, int position) {
        if (displayedUris != null) {
            Uri uri = displayedUris.get(position);
            holder.imageView.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {
        return (displayedUris != null) ? displayedUris.size() : 0;
    }

    public ArrayList<Uri> getDisplayedUris() {
        return displayedUris;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
        }
    }

    // Add this method to update displayed images
    public void updateDisplayedUris(ArrayList<Uri> updatedUris) {
        displayedUris.clear();
        displayedUris.addAll(updatedUris);
        notifyDataSetChanged();
    }
}
