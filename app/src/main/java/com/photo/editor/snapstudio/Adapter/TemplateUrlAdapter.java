package com.photo.editor.snapstudio.Adapter;

import static com.photo.editor.snapstudio.Templates.MainActivity.imgid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.PhEditor.Model.Templates;
import com.photo.editor.snapstudio.PhEditor.utils.Constant;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.Templates.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TemplateUrlAdapter extends RecyclerView.Adapter<TemplateUrlAdapter.ViewHolder> {

    Context context;
    ArrayList<Templates> itemList;
    String queryString;

    public TemplateUrlAdapter(Context context, ArrayList<Templates> itemList, String queryString) {
        this.context = context;
        this.itemList = itemList;
        this.queryString = queryString;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(itemList.get(position).getImageUrl()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (!Constant.IS_SUBSCRIBED){
                imgid = 1;
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("imageURI", itemList.get(position).getImageUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
               /* }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Please subscribe to get premium items!!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.startActivity(new Intent(context, PremiumActivity.class));
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.black)));
                        }
                    });
                    alertDialog.show();
                }*/

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.titems_img);

        }
    }
}
