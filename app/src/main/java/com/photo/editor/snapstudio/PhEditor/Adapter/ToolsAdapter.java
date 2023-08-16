package com.photo.editor.snapstudio.PhEditor.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.tools.ToolEditor;

import java.util.ArrayList;
import java.util.List;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.ViewHolder> {

    public OnItemSelected onItemSelected;
    public List<ToolModel> toolLists = new ArrayList<>();
    public int selectedSquareIndex = 0;
    Context context;

    public interface OnItemSelected {
        void onToolSelected(ToolEditor toolType);
    }

    public ToolsAdapter(Context context,OnItemSelected onItemSelected) {
        this.context = context;
        this.onItemSelected = onItemSelected;
        this.toolLists.add(new ToolModel(R.string.filter, R.drawable.ic_filter, ToolEditor.FILTER));
        this.toolLists.add(new ToolModel(R.string.adjust, R.drawable.adjust, ToolEditor.ADJUST));
        this.toolLists.add(new ToolModel(R.string.drip, R.drawable.ic_drip, ToolEditor.DRIP));
        this.toolLists.add(new ToolModel(R.string.overlay, R.drawable.ic_overlay, ToolEditor.EFFECT));
        this.toolLists.add(new ToolModel(R.string.bg_change, R.drawable.ic_background, ToolEditor.BG));
        this.toolLists.add(new ToolModel(R.string.neon, R.drawable.ic_neon, ToolEditor.NEON));
        this.toolLists.add(new ToolModel(R.string.crop, R.drawable.tools, ToolEditor.CROP));
        this.toolLists.add(new ToolModel(R.string.profile, R.drawable.ic_profile, ToolEditor.PROFILE));
        this.toolLists.add(new ToolModel(R.string.beauty, R.drawable.ic_face_beauty, ToolEditor.BEAUTY));
        this.toolLists.add(new ToolModel(R.string.splash, R.drawable.ic_splash, ToolEditor.SPLASH));
        this.toolLists.add(new ToolModel(R.string.hsl, R.drawable.ic_hsl, ToolEditor.HSL));
        this.toolLists.add(new ToolModel(R.string.body, R.drawable.ic_seat, ToolEditor.BODY));
        this.toolLists.add(new ToolModel(R.string.wing, R.drawable.ic_wing, ToolEditor.WINGS));
        this.toolLists.add(new ToolModel(R.string.sticker, R.drawable.ic_sticker, ToolEditor.STICKER));
        this.toolLists.add(new ToolModel(R.string.text, R.drawable.text, ToolEditor.TEXT));
        this.toolLists.add(new ToolModel(R.string.blur, R.drawable.ic_blur, ToolEditor.BLUR));
        this.toolLists.add(new ToolModel(R.string.mirror, R.drawable.ic_mirror, ToolEditor.MIRROR));
        this.toolLists.add(new ToolModel(R.string.paint, R.drawable.ic_paint, ToolEditor.PAINT));
        this.toolLists.add(new ToolModel(R.string.ratio, R.drawable.ic_ratio, ToolEditor.RATIO));
        this.toolLists.add(new ToolModel(R.string.square, R.drawable.ic_blur_bg, ToolEditor.SQUARE));
    }

    class ToolModel {
        public int toolIcon;
        public int toolName;
        public ToolEditor toolType;

        ToolModel(int str, int i, ToolEditor toolType) {
            this.toolName = str;
            this.toolIcon = i;
            this.toolType = toolType;
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_editing, viewGroup, false));
    }

    public void reset() {
        this.selectedSquareIndex = 0;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ToolModel toolModel = this.toolLists.get(i);
        viewHolder.text_view_tool_name.setText(toolModel.toolName);
        viewHolder.image_view_tool_icon.setImageResource(toolModel.toolIcon);

        boolean isDarkTheme = context.getSharedPreferences(context.getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkTheme){
            viewHolder.image_view_tool_icon.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            viewHolder.text_view_tool_name.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            viewHolder.image_view_tool_icon.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.blacktwo)));
            viewHolder.text_view_tool_name.setTextColor(context.getResources().getColor(R.color.blacktwo));
        }

        /*if (this.selectedSquareIndex == i) {
            viewHolder.text_view_tool_name.setTextColor(Color.parseColor("#0004FF"));
            viewHolder.image_view_tool_icon.setColorFilter(Color.parseColor("#0004FF"));
            return;
        } else {
            viewHolder.text_view_tool_name.setTextColor(Color.parseColor("#000000"));
            viewHolder.image_view_tool_icon.setColorFilter(Color.parseColor("#000000"));
        }*/
    }

    public int getItemCount() {
        return this.toolLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view_tool_icon;
        TextView text_view_tool_name;
        RelativeLayout relative_layout_wrapper_tool;

        ViewHolder(View view) {
            super(view);
            this.image_view_tool_icon = view.findViewById(R.id.image_view_tool_icon);
            this.text_view_tool_name = view.findViewById(R.id.text_view_tool_name);
            this.relative_layout_wrapper_tool = view.findViewById(R.id.relative_layout_wrapper_tool);
            relative_layout_wrapper_tool.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ToolsAdapter.this.selectedSquareIndex = ViewHolder.this.getLayoutPosition();
                    ToolsAdapter.this.onItemSelected.onToolSelected((ToolsAdapter.this.toolLists.get(ToolsAdapter.this.selectedSquareIndex)).toolType);
                    notifyDataSetChanged();
                }
            });

        }
    }
}
