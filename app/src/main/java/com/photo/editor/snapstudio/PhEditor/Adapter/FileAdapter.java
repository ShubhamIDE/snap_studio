package com.photo.editor.snapstudio.PhEditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.listener.FileClickInterface;

import java.io.File;
import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private Context context;
    private ArrayList<File> files;
    private FileClickInterface fileListClickInterface;

    public FileAdapter(Context context, ArrayList<File> files, FileClickInterface fileListClickInterface) {
        this.context = context;
        this.files = files;
        this.fileListClickInterface=  fileListClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        File fileItem = files.get(i);

        try {
            String extension = fileItem.getName().substring(fileItem.getName().lastIndexOf("."));
            Glide.with(context)
                    .load(fileItem.getPath())
                    .into(viewHolder.imageViewCover);
        }catch (Exception ex){
        }

        viewHolder.relativeLayoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileListClickInterface.getPosition(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return files == null ? 0 : files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayoutContent;
        public ImageView imageViewCover;

        public ViewHolder(View view) {
            super(view);
            this.relativeLayoutContent = view.findViewById(R.id.relativeLayoutContent);
            this.imageViewCover = view.findViewById(R.id.imageViewCover);
        }
    }

}