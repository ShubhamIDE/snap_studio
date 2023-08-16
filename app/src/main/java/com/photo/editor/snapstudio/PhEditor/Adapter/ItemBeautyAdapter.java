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

public class ItemBeautyAdapter extends RecyclerView.Adapter<ItemBeautyAdapter.ViewHolder> {

    public OnItemEyeSelected onItemSelected;
    public List<ToolModel> toolLists = new ArrayList<>();
    public int selectedSquareIndex = 0;

    Context context;
    public interface OnItemEyeSelected {
        void onToolEyeSelected(ToolEditor toolType);
    }

    public ItemBeautyAdapter(Context context,OnItemEyeSelected onItemSelected) {
        this.onItemSelected = onItemSelected;
        this.context = context;
        this.toolLists.add(new ToolModel(R.string.eye, R.drawable.ic_color_eyes, ToolEditor.EYE_BEAUTY));
        this.toolLists.add(new ToolModel(R.string.lip, R.drawable.ic_lip_beauty, ToolEditor.LIP_BEAUTY));
        this.toolLists.add(new ToolModel(R.string.face, R.drawable.ic_face_beauty, ToolEditor.FACE_BEAUTY));
        this.toolLists.add(new ToolModel(R.string.whiten, R.drawable.ic_whiten_beauty, ToolEditor.WHITEN_BEAUTY));
        this.toolLists.add(new ToolModel(R.string.cheek, R.drawable.ic_cheek_beauty, ToolEditor.CHEEK_BEAUTY));
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
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_edit_beauty, viewGroup, false));
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
//            mainLayout.setBackgroundResource(R.color.blacktwo);
            viewHolder.image_view_tool_icon.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            viewHolder.text_view_tool_name.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));

        }else {
//            mainLayout.setBackgroundResource(R.color.white);
            viewHolder.image_view_tool_icon.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.blacktwo)));
            viewHolder.text_view_tool_name.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.blacktwo)));

        }
        /*if (this.selectedSquareIndex == 0) {
            viewHolder.text_view_tool_name.setTextColor(Color.parseColor("#151515"));
            viewHolder.image_view_tool_icon.setColorFilter(Color.parseColor("#151515"));
            viewHolder.relative_layout_edit.setBackgroundResource(R.drawable.background_item);
            return;
        } else if (this.selectedSquareIndex == 1) {
            viewHolder.text_view_tool_name.setTextColor(Color.parseColor("#FFFFFF"));
            viewHolder.relative_layout_edit.setBackgroundResource(R.drawable.background_item_selected);
            viewHolder.image_view_tool_icon.setColorFilter(Color.parseColor("#FFFFFF"));
            return;
        } else {
            viewHolder.text_view_tool_name.setTextColor(Color.parseColor("#151515"));
            viewHolder.image_view_tool_icon.setColorFilter(Color.parseColor("#151515"));
            viewHolder.relative_layout_edit.setBackgroundResource(R.drawable.background_item);
        }

         */
    }

    public int getItemCount() {
        return this.toolLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view_tool_icon;
        TextView text_view_tool_name;
        RelativeLayout relative_layout_wrapper_tool;
        RelativeLayout relative_layout_edit;

        ViewHolder(View view) {
            super(view);
            this.image_view_tool_icon = view.findViewById(R.id.image_view_tool_icon);
            this.text_view_tool_name = view.findViewById(R.id.text_view_tool_name);
            this.relative_layout_wrapper_tool = view.findViewById(R.id.relative_layout_wrapper_tool);
            this.relative_layout_edit = view.findViewById(R.id.relativeLayoutEdit);
            relative_layout_wrapper_tool.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ItemBeautyAdapter.this.selectedSquareIndex = ViewHolder.this.getLayoutPosition();
                    ItemBeautyAdapter.this.onItemSelected.onToolEyeSelected((ItemBeautyAdapter.this.toolLists.get(ItemBeautyAdapter.this.selectedSquareIndex)).toolType);
                    notifyDataSetChanged();
                }
            });

        }
    }
}
