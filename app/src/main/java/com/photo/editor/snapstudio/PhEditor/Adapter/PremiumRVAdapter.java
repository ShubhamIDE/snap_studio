//package com.photo.editor.snapstudio.PhEditor.Adapter;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.SystemClock;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.photo.editor.snapstudio.Activity.PremiumActivity;
//import com.photo.editor.snapstudio.Model.PremiumItems;
//import com.photo.editor.snapstudio.R;
//import com.photo.editor.snapstudio.Store.SEditorActivity;
//import com.photo.editor.snapstudio.collage.Activity.SelectImageActivity;
//import com.photo.editor.snapstudio.utils.Constant;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//
//public class PremiumRVAdapter extends RecyclerView.Adapter<PremiumRVAdapter.ViewHolder>{
//
//    Context context;
//    ArrayList<PremiumItems> itemList;
//    String queryString;
//
//    public PremiumRVAdapter(Context context, ArrayList<PremiumItems> itemList, String queryString) {
//        this.context = context;
//        this.itemList = itemList;
//        this.queryString = queryString;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pitems, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Picasso.get().load(itemList.get(position).getItemUrl()).into(holder.imageView);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (Constant.IS_SUBSCRIBED){
//                    if (queryString == "bg"){
//                        checkClick();
//                        Intent intent = new Intent(context, SelectImageActivity.class);
//                        intent.putExtra("bgUrl", itemList.get(position).getItemUrl());
////                            Intent intent = new Intent(getActivity(), GalleryActivity.class);
//                        intent.putExtra("rvValues", "changebg");
//                        context.startActivity(intent);
//                    }else {
//                        Intent intent = new Intent(context, SEditorActivity.class);
//                        intent.putExtra("imgUrl", itemList.get(position).getItemUrl());
//                        intent.putExtra("queryString", queryString);
//                        context.startActivity(intent);
//                    }
//                }else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setMessage("Please subscribe to get premium items!!");
//                    builder.setCancelable(false);
//                    builder.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            context.startActivity(new Intent(context, PremiumActivity.class));
//                        }
//                    });
//                    builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
//                        @Override
//                        public void onShow(DialogInterface arg0) {
//                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
//                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
//                        }
//                    });
//                    alertDialog.show();
//                }
//
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return itemList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.pitems_img);
//
//        }
//    }
//    private long mLastClickTime = 0;
//
//    private void checkClick() {
//        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//            return;
//        }
//        mLastClickTime = SystemClock.elapsedRealtime();
//    }
//}
