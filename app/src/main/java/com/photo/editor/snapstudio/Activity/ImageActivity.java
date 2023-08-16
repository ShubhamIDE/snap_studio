package com.photo.editor.snapstudio.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.photo.editor.snapstudio.BuildConfig;
import com.photo.editor.snapstudio.PhEditor.Activity.EditorActivity;
import com.photo.editor.snapstudio.PhEditor.picker.PhotoPicker;
import com.photo.editor.snapstudio.R;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    ImageView backbt, morebt, imageShow;
    String imageuri;

    private File saved_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_image);
        backbt = findViewById(R.id.backbt);
        morebt = findViewById(R.id.menu);
        imageShow = findViewById(R.id.imageShow);

        imageuri = getIntent().getStringExtra("image_uri");

        saved_file = new File(imageuri);
        Glide.with(this).load(imageuri).into(imageShow);

        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        morebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreDialog();
            }
        });

    }

    private void moreDialog() {
        Dialog dialogOnBackPressed = new Dialog(ImageActivity.this);
        dialogOnBackPressed.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOnBackPressed.setCancelable(true);
        dialogOnBackPressed.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogOnBackPressed.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        dialogOnBackPressed.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialogOnBackPressed.setContentView(R.layout.dialog_menu);
        TextView textViewEdit, textViewShare, textViewDelete;
        textViewEdit = dialogOnBackPressed.findViewById(R.id.textViewEdit);
        textViewShare = dialogOnBackPressed.findViewById(R.id.textViewShare);
        textViewDelete = dialogOnBackPressed.findViewById(R.id.textViewDelete);

//        textViewDelete.setVisibility(View.GONE);

        textViewEdit.setOnClickListener(view -> {
            String uri = imageuri;
            try {
                Intent i = new Intent(ImageActivity.this, EditorActivity.class);
                i.putExtra(PhotoPicker.KEY_SELECTED_PHOTOS, uri);
//                i.putExtra("imgPath", mSelectedImages.get(position))
                i.putExtra("rvValue", "edit");
                i.putExtra("bgUrl", "");
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        textViewShare.setOnClickListener(view -> {
            String uri = imageuri;
            File saved_file = new File(uri);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.putExtra(
                    "android.intent.extra.STREAM",
                    FileProvider.getUriForFile(
                            ImageActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            saved_file
                    )
            );
            share.setType("image/*");
            startActivity(Intent.createChooser(share, "Share Image"));
        });

        textViewDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
            builder.setMessage("Are you sure you want to delete?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String filepath = imageuri;
                    if (new File(filepath).delete()) {
//                                paths.remove(position);
//                                notifyDataSetChanged();
                    }
                    dialog.dismiss();
                    dialogOnBackPressed.dismiss();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

        });

    /*    textViewCancel.setOnClickListener(view -> {
            dialogOnBackPressed.dismiss();
        });

        textViewDiscard.setOnClickListener(view -> {
            dialogOnBackPressed.dismiss();
            startActivity(new Intent(requireActivity(), DashboardActivity.class));
            EditorActivity.this.finish();
        });*/

        dialogOnBackPressed.show();
    }
}