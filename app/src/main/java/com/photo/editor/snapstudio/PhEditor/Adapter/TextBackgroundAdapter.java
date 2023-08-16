package com.photo.editor.snapstudio.PhEditor.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.assets.BrushColorAsset;

import java.util.ArrayList;
import java.util.List;

public class TextBackgroundAdapter extends RecyclerView.Adapter<TextBackgroundAdapter.ViewHolder> {
    public blacktwoListener blacktwoListener;
    private Context context;
    public int selectedSquareIndex;
    public List<SquareView> squareViewList = new ArrayList();

    public interface blacktwoListener {
        void onblacktwoSelected(int i, SquareView squareView);
    }

    public TextBackgroundAdapter(Context context, blacktwoListener frameListener) {
        this.context = context;
        this.blacktwoListener = frameListener;
        List<String> listColorBrush = BrushColorAsset.listColorBrush();
        for (int i = 0; i < listColorBrush.size() - 2; i++) {
            this.squareViewList.add(new SquareView(Color.parseColor(listColorBrush.get(i)),  true));
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color_text, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SquareView squareView = this.squareViewList.get(i);
        if (squareView.isColor) {
            viewHolder.squareView.setCardBackgroundColor(squareView.drawableId);
        } else {
            viewHolder.squareView.setCardBackgroundColor(squareView.drawableId);
        }
        if (this.selectedSquareIndex == i) {
            viewHolder.viewSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.viewSelected.setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        return this.squareViewList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CardView squareView;
        ImageView viewSelected;
        public ConstraintLayout wrapSquareView;

        public ViewHolder(View view) {
            super(view);
            this.squareView = view.findViewById(R.id.card);
            this.viewSelected = view.findViewById(R.id.view_selected);
            this.wrapSquareView = view.findViewById(R.id.constraint_layout_wrapper_square_view);

            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            TextBackgroundAdapter.this.selectedSquareIndex = getAdapterPosition();
            TextBackgroundAdapter.this.blacktwoListener.onblacktwoSelected(TextBackgroundAdapter.this.selectedSquareIndex, TextBackgroundAdapter.this.squareViewList.get(TextBackgroundAdapter.this.selectedSquareIndex));
            TextBackgroundAdapter.this.notifyDataSetChanged();
        }
    }

    public class SquareView {
        public int drawableId;
        public boolean isColor;

        SquareView(int i) {
            this.drawableId = i;
        }

        SquareView(int i, boolean z) {
            this.drawableId = i;
            this.isColor = z;
        }
    }
}
