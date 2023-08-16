package com.photo.editor.snapstudio.PhEditor.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.photo.editor.snapstudio.R;

//import com.coding.photo.edit.R;

public class ColorListAdapter extends Adapter<ColorListAdapter.ViewHolder> {
    public int selected = 0;
    private boolean IsStickerSeleted = false;
    private int[] colorSet;

    public ColorListAdapter(Context context, int[] iArr, boolean z) {
        this.colorSet = iArr;
        this.IsStickerSeleted = z;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color_beauty, null));
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (this.IsStickerSeleted) {
            viewHolder.imageView.setBackgroundColor(Color.parseColor("#00ffffff"));
            viewHolder.imageView.setBackgroundColor(this.colorSet[i]);
        } else {
            viewHolder.imageView.setBackgroundColor(this.colorSet[i]);
        }
        viewHolder.imageView.invalidate();
        if (i == this.selected) {
            viewHolder.selectedImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.selectedImageView.setVisibility(View.GONE);
        }
        viewHolder.filterRoot.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ColorListAdapter.this.selected != i) {
                    int access$000 = ColorListAdapter.this.selected;
                    ColorListAdapter.this.selected = i;
                    ColorListAdapter.this.notifyItemChanged(access$000);
                    ColorListAdapter.this.notifyItemChanged(i);
                }
            }
        });
    }

    public int getItemCount() {
        return this.colorSet.length;
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public RelativeLayout filterRoot;
        public View imageView;
        public ImageView selectedImageView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (View) view.findViewById(R.id.square_view);
            this.selectedImageView = (ImageView) view.findViewById(R.id.imageSelection);
            this.filterRoot = (RelativeLayout) view.findViewById(R.id.filterRoot);
        }
    }
}
