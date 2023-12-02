package com.example.blackbox;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder> {
    private ArrayList<Uri> displayedUris;  // Track displayed images
    private Context context;
    public ImageRecyclerAdapter(Context context, ArrayList<Uri> displayedUris) {
        this.context = context;
        this.displayedUris = displayedUris;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView imageRemoveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            imageRemoveButton = itemView.findViewById(R.id.crossButton);
        }
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
            holder.imageRemoveButton.setOnClickListener(view -> {
                Log.d("Delete Image", "Delete image at position " + position);
                showConfirmationDialog(position);
            });
        }
    }

    private void showConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to remove this image?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    removeItem(position);
                }).setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Method to remove an item at a specific position
    private void removeItem(int position) {
        displayedUris.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (displayedUris != null) ? displayedUris.size() : 0;
    }

    // Add this method to update displayed images
    public void updateDisplayedUris(ArrayList<Uri> updatedUris) {
        displayedUris.clear();
        displayedUris.addAll(updatedUris);
        Log.d("Attach image", "Updating image");
        notifyDataSetChanged();
    }


}


