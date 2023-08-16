package com.photo.editor.snapstudio.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.photo.editor.snapstudio.AiSearch.AppInterfaces;
import com.photo.editor.snapstudio.PhEditor.Activity.EditorActivity;
import com.photo.editor.snapstudio.PhEditor.picker.PhotoPicker;
import com.photo.editor.snapstudio.R;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private static final String TAG = "SearchResultAdapter";

    Context context;
    ArrayList<String> arrayList;
    AppInterfaces.SearchResultOnClick searchResultOnClick;

    public SearchResultAdapter(Context context, ArrayList<String> arrayList, AppInterfaces.SearchResultOnClick searchResultOnClick) {
        this.context = context;
        this.arrayList = arrayList;
        this.searchResultOnClick = searchResultOnClick;
    }

//    public SearchResultAdapter(Context context, ArrayList<String> arrayList) {
//        this.context = context;
//        this.arrayList = arrayList;
//    }

    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_ai_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ViewHolder holder, int position) {
        String imgURL = arrayList.get(position);
        Log.println(Log.ASSERT, TAG, "imgURL " + position + ": " + imgURL);


        Glide.with(context).load(imgURL).into(holder.imgResult);

        boolean isDarkTheme = context.getSharedPreferences(context.getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
//            holder.save.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            holder.edit.setImageResource(R.drawable.edit_dark);
        } else {
//            holder.save.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
            holder.edit.setImageResource(R.drawable.edit_light);
        }

        holder.imgResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchResultOnClick.getImageUrl(imgURL);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchResultOnClick.editImage(imgURL);
//                context.startActivity(new Intent(context, EditorActivity.class).putExtra("rvValues", "edit").putExtra("bgUrl", "").putExtra(PhotoPicker.KEY_SELECTED_PHOTOS, imgURL));

            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchResultOnClick.downloadImage(imgURL);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgResult, save, edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgResult = itemView.findViewById(R.id.img_result);
            save = itemView.findViewById(R.id.save_result);
            edit = itemView.findViewById(R.id.edit_result);
        }
    }

}
