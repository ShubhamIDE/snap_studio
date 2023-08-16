package com.photo.editor.snapstudio.PhEditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.PhEditor.Model.HFModel;
import com.photo.editor.snapstudio.R;

import java.util.ArrayList;

public class HfAdapter extends RecyclerView.Adapter<HfAdapter.ViewHolder> {
    public ArrayList<HFModel> hfLists;
    Context context;

    public HfAdapter(Context context, ArrayList<HFModel> hfLists) {
        this.context = context;
        this.hfLists = hfLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hf, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.hfimg.setImageResource(hfLists.get(position).hfimage);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView hfimg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.hfimg = itemView.findViewById(R.id.hfimg);
        }
    }
}
