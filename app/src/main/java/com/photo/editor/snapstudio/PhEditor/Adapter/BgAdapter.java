package com.photo.editor.snapstudio.PhEditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.listener.LayoutItemListener;

import java.util.ArrayList;

public class BgAdapter extends Adapter<BgAdapter.ViewHolder> {

    public LayoutItemListener menuItemClickLister;
    public int selectedItem = 0;
    Context context;
    private ArrayList<String> wingsIcons = new ArrayList<>();

    public BgAdapter(Context mContext) {
        this.context = mContext;
    }

    public void addData(ArrayList<String> arrayList) {
        this.wingsIcons.clear();
        this.wingsIcons.addAll(arrayList);
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bg_change, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mSelectedBorder.setVisibility(position == selectedItem ? View.VISIBLE : View.GONE);

        String file_1 = "file:///android_asset/bg/" + wingsIcons.get(position) + ".png";
        Glide.with(context)
                .load(file_1)
                .fitCenter()
                .into(holder.imageViewItem1);
    }

    public int getItemCount() {
        return wingsIcons.size();
    }

    public ArrayList<String> getItemList() {
        return wingsIcons;
    }

    public void setMenuItemClickLister(LayoutItemListener menuItemClickLister) {
        this.menuItemClickLister = menuItemClickLister;
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {

        View mSelectedBorder;
        RoundedImageView imageViewItem1;

        ViewHolder(View view) {
            super(view);
            imageViewItem1 = view.findViewById(R.id.imageViewItem1);
            mSelectedBorder = view.findViewById(R.id.selectedBorder);
            view.setTag(view);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            int p = selectedItem;
            selectedItem = getAdapterPosition();
            notifyItemChanged(p);
            notifyItemChanged(selectedItem);
            menuItemClickLister.onLayoutListClick(view, getAdapterPosition());
        }
    }
}
