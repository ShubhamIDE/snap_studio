package com.photo.editor.snapstudio.PhEditor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.constants.Constants;
import com.photo.editor.snapstudio.PhEditor.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

public class MirrorAdapter extends RecyclerView.Adapter<MirrorAdapter.ViewHolder> {
    private Context context;
    public Mirror3Listener frameListener;
    public int selectedSquareIndex;
    public List<SquareView> squareViewList;
    private int borderWidth;

    public interface Mirror3Listener {
        void onMirrorSelected(int i, SquareView squareView);
    }

    public MirrorAdapter(Context context2, Mirror3Listener frameListener2) {
        ArrayList arrayList = new ArrayList();
        this.squareViewList = arrayList;
        this.context = context2;
        this.frameListener = frameListener2;
        arrayList.add(new SquareView((int) R.drawable.mirror_3d_1_icon, "3D-1", (int) R.drawable.mirror_3d_1));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_2_icon, "3D-2", (int) R.drawable.mirror_3d_2));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_3_icon, "3D-3", (int) R.drawable.mirror_3d_3));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_4_icon, "3D-4", (int) R.drawable.mirror_3d_4));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_5_icon, "3D-5", (int) R.drawable.mirror_3d_5));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_6_icon, "3D-6", (int) R.drawable.mirror_3d_6));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_7_icon, "3D-7", (int) R.drawable.mirror_3d_7));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_8_icon, "3D-8", (int) R.drawable.mirror_3d_8));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_9_icon, "3D-9", (int) R.drawable.mirror_3d_9));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_10_icon, "3D-10", (int) R.drawable.mirror_3d_10));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_11_icon, "3D-11", (int) R.drawable.mirror_3d_11));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_12_icon, "3D-12", (int) R.drawable.mirror_3d_12));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_11_icon, "3D-13", (int) R.drawable.mirror_3d_11));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_12_icon, "3D-14", (int) R.drawable.mirror_3d_12));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_10_icon, "3D-15", (int) R.drawable.mirror_3d_10));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_11_icon, "3D-16", (int) R.drawable.mirror_3d_11));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_12_icon, "3D-17", (int) R.drawable.mirror_3d_12));
        this.squareViewList.add(new SquareView((int) R.drawable.mirror_3d_10_icon, "3D-18", (int) R.drawable.mirror_3d_10));
        this.borderWidth = SystemUtil.dpToPx(context2, Constants.BORDER_WIDTH);

    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mirror, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.round_image_view_filter_item.setImageResource(this.squareViewList.get(i).drawableId);
        if (this.selectedSquareIndex == i) {
            viewHolder.wrapSquareView.setBackgroundResource(R.drawable.background_item_selected);
            return;
        }
        viewHolder.wrapSquareView.setBackgroundResource(R.drawable.background_item);
    }

    public int getItemCount() {
        return this.squareViewList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView round_image_view_filter_item;
        public RelativeLayout wrapSquareView;

        public ViewHolder(View view) {
            super(view);
            this.round_image_view_filter_item = view.findViewById(R.id.round_image_view_splash_item);
            this.wrapSquareView =  view.findViewById(R.id.relativeLayoutImage);

            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            MirrorAdapter.this.selectedSquareIndex = getAdapterPosition();
            MirrorAdapter.this.frameListener.onMirrorSelected(MirrorAdapter.this.selectedSquareIndex, MirrorAdapter.this.squareViewList.get(MirrorAdapter.this.selectedSquareIndex));
            MirrorAdapter.this.notifyDataSetChanged();
        }
    }

    public class SquareView {
        public int drawableId;
        public int mirror;
        public String text;

        SquareView(int i, String string, int mirror2) {
            this.drawableId = i;
            this.text = string;
            this.mirror = mirror2;
        }

        SquareView(int i, String string, boolean z) {
            this.drawableId = i;
            this.text = string;
        }
    }
}
