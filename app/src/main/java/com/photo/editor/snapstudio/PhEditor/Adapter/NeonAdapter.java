package com.photo.editor.snapstudio.PhEditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.listener.LayoutItemListener;

import java.util.ArrayList;

public class NeonAdapter extends Adapter<NeonAdapter.ViewHolder> {

    public LayoutItemListener layoutItenListener;
    public int selectedItem = 0;
    Context context;
    private ArrayList<String> neonIcons = new ArrayList<>();

    public NeonAdapter(Context mContext) {
        this.context = mContext;
    }

    public void addData(ArrayList<String> arrayList) {
        this.neonIcons.clear();
        this.neonIcons.addAll(arrayList);
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_neon, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSelectedBorder.setVisibility(position == selectedItem ? View.VISIBLE : View.GONE);
        String sb2 = "file:///android_asset/neon/" + neonIcons.get(position) +"_front.webp";
        String sb3 = "file:///android_asset/neon/" + neonIcons.get(position) +"_back.webp";
        Glide.with(context)
                .load(sb2)
                .fitCenter()
                .into(holder.imageViewItem1);
        Glide.with(context)
                .load(sb3)
                .fitCenter()
                .into(holder.imageViewItem2);
    }

    public int getItemCount() {
        return neonIcons.size();
    }

    public ArrayList<String> getItemList() {
        return neonIcons;
    }

    public void setLayoutItenListener(LayoutItemListener layoutItenListener) {
        this.layoutItenListener = layoutItenListener;
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {

        View mSelectedBorder;
        ImageView imageViewItem1;
        ImageView imageViewItem2;

        ViewHolder(View view) {
            super(view);
            imageViewItem1 = view.findViewById(R.id.imageViewItem1);
            imageViewItem2 = view.findViewById(R.id.imageViewItem2);
            mSelectedBorder = view.findViewById(R.id.selectedBorder);
            view.setTag(view);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            int p = selectedItem;
            selectedItem = getAdapterPosition();
            notifyItemChanged(p);
            notifyItemChanged(selectedItem);
            layoutItenListener.onLayoutListClick(view, getAdapterPosition());
        }
    }
}
