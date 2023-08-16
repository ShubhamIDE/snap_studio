//package com.photo.editor.snapstudio.PhEditor.Adapter;
////
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.photo.editor.snapstudio.Model.Images;
//import com.photo.editor.snapstudio.R;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//
//public class PlAdapter extends RecyclerView.Adapter<PlAdapter.ViewHolder> {
//
//    private ArrayList<Images> cities;
//
//    public PlAdapter(ArrayList<Images> cities) {
//        this.cities = cities;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.pl_item_design, parent, false);
//
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Images city = cities.get(position);
//
//        holder.name.setText(city.getName());
//        holder.description.setText(city.getDescription());
//
//        Picasso.get().load(city.getImageURL()).into(holder.image);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (cities != null) {
//            return cities.size();
//        } else {
//            return 0;
//        }
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public final View view;
//        public final TextView name;
//        public final TextView description;
//        public final ImageView image;
//
//        public ViewHolder(View view) {
//            super(view);
//            this.view = view;
//            name = view.findViewById(R.id.name);
//            description = view.findViewById(R.id.description);
//            image = view.findViewById(R.id.image);
//        }
//    }
//}