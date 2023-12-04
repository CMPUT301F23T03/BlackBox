package com.example.blackbox.inventory.filter;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private int spacingInDp;
    private int spacing;

    public SpacingItemDecoration(Context context, int spacingInDp) {
        this.context = context;
        this.spacingInDp = spacingInDp;
        this.spacing = dpToPx(spacingInDp);
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();

        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        // Add spacing to the bottom for all items except the last one
        if (position < itemCount - 1) {
            if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                outRect.bottom = spacing;
            } else {
                outRect.right = spacing;
            }
        } else {
            // Add margin to the right of the last item
            if (layoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                outRect.right = spacing;
            }
        }

        // Add spacing to the top for the first item
        if (position == 0) {
            if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                outRect.top = spacing;
            } else {
                outRect.left = spacing / 2;
            }
        }
    }
}

