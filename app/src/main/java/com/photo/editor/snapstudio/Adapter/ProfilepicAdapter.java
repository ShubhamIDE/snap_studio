package com.photo.editor.snapstudio.Adapter;

import static com.photo.editor.snapstudio.Global.isEmptyStr;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ProfilepicAdapter extends RecyclerView.Adapter<ProfilepicAdapter.MyViewHolder> {
    Context context;
    OnClickListener onClickListener;
    ArrayList<String> mData = new ArrayList<String>();
    int mPos;

    public void clearSelection() {
        mPos = -1;
        notifyDataSetChanged();
    }

    public int getPos() {
        return mPos;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mRLSelect;
        ImageView mIvAnim;

        public MyViewHolder(View view) {
            super(view);
            mRLSelect = view.findViewById(R.id.mRLSelect);
            mIvAnim = view.findViewById(R.id.mIvAnim);
        }
    }

    public ProfilepicAdapter(Context con, ArrayList<String> stringArray, OnClickListener mListener, int mPos) {
        this.context = con;
        this.onClickListener = mListener;
        this.mData = stringArray;
        this.mPos = mPos;
    }

    /*public ProfilepicAdapter(Context con, ArrayList<String> stringArray, OnClickListener mListener) {
        context = con;
        mData.addAll(stringArray);
        onClickListener = mListener;
    }*/

    @NonNull
    @Override
    public ProfilepicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfilepicAdapter.MyViewHolder holder, final int position) {
        String path = mData.get(position);
        if (!isEmptyStr(path)) {
//            onClickListener.onLoadAnim(mData.get(0));
            InputStream inputstream = null;
            try {
                inputstream = context.getAssets().open("Anims/" + path);
                Drawable drawable = Drawable.createFromStream(inputstream, null);
                holder.mIvAnim.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mPos == position) {
                holder.mRLSelect.setVisibility(View.VISIBLE);
            } else {
            holder.mRLSelect.setVisibility(View.GONE);
            }
            holder.mIvAnim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPos = position;
                    notifyDataSetChanged();
                    onClickListener.onclickAnim(path, mPos);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnClickListener {
        void onclickAnim(String string, int mPos);
//        void onLoadAnim(String string);
    }
}
