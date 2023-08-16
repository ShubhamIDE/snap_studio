package com.photo.editor.snapstudio.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.photo.editor.snapstudio.Activity.EditProfileActivity;
import com.photo.editor.snapstudio.R;

import java.util.ArrayList;

public class GenderAdapter extends ArrayAdapter<String> {

    public GenderAdapter(@NonNull Context context, ArrayList<String> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.age_spinner_item, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.age_num);

        textViewName.setText(getItem(position));

        boolean isDarkThemes = getContext().getSharedPreferences(getContext().getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);

        if (isDarkThemes) {
            textViewName.setTextColor(getContext().getResources().getColor(R.color.greyt));
        } else {
            textViewName.setTextColor(getContext().getResources().getColor(R.color.blacktwo));
        }


        return convertView;
    }
}
