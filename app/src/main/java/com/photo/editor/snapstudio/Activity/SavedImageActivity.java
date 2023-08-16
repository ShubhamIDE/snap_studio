package com.photo.editor.snapstudio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.BuildConfig;
import com.photo.editor.snapstudio.PhEditor.Activity.EraseActivity;
import com.photo.editor.snapstudio.R;

import java.io.File;

public class SavedImageActivity extends AppCompatActivity {

    private File saved_file;
    LinearLayout mainLayout;
    TextView txt, txt2, sharebt, txt3;
    ImageView backbt, home;
    boolean isDarkTheme;

    String imageuri;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_saved_image);
        mainLayout = findViewById(R.id.main_layout);
        txt = findViewById(R.id.txt);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        backbt = findViewById(R.id.backbt);
        home = findViewById(R.id.home);
        sharebt = findViewById(R.id.share_bt);
        imageView = findViewById(R.id.img_show);
        changeTheme();
        imageuri = getIntent().getStringExtra("image_uri");

        saved_file = new File(imageuri);
        Glide.with(this).load(imageuri).into(imageView);

        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(SavedImageActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        AdUtils.showInterstitialAd(SavedImageActivity.this, new AppInterfaces.InterstitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {
                SavedImageActivity.super.onBackPressed();
            }
        });
    }

    private void changeTheme() {
        isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        AdUtils.showNativeAd(SavedImageActivity.this, findViewById(R.id.native_ads), false, isDarkTheme, false);

        if (isDarkTheme) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.blacktwo));
            txt.setTextColor(getResources().getColor(R.color.white));
            txt2.setTextColor(Color.parseColor("#80FFFFFF"));
            txt3.setTextColor(Color.parseColor("#80FFFFFF"));
            sharebt.setTextColor(getResources().getColor(R.color.blacktwo));
            sharebt.setBackgroundResource(R.drawable.curvebt3);
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            home.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            txt.setTextColor(getResources().getColor(R.color.blacktwo));
            txt2.setTextColor(getResources().getColor(R.color.greyt));
            txt3.setTextColor(getResources().getColor(R.color.greyt));
            sharebt.setTextColor(getResources().getColor(R.color.white));
            sharebt.setBackgroundResource(R.drawable.curvebt);
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            home.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
        }
    }


    public void goSubs(View view) {
        startActivity(new Intent(getApplicationContext(), SubscriptionActivity.class));
    }

    public void bottomSheetDialog(View view) {

//        final RoundedBottomSheetDialog bottomSheetDialog = new RoundedBottomSheetDialog(SavedImageActivity.this);
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View sheetView = inflater.inflate(R.layout.bottom_sheet_dialog, null);
//        bottomSheetDialog.setContentView(sheetView);
//        bottomSheetDialog.setCanceledOnTouchOutside(true);
//        bottomSheetDialog.show();
//
//        TextView insta, fb, twitter, whatsapp, more, sharebtn;
//        insta = sheetView.findViewById(R.id.insta);
//        fb = sheetView.findViewById(R.id.fb);
//        twitter = sheetView.findViewById(R.id.twitter);
//        whatsapp = sheetView.findViewById(R.id.whatsapp);
//        more = sheetView.findViewById(R.id.more);
//        sharebtn = sheetView.findViewById(R.id.sharebtn);
//
//        whatsapp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                shareImageSocialApp("com.whatsapp", "Whatsapp");
//            }
//
//        });
//        insta.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                shareImageSocialApp(
//                        "com.instagram.android",
//                        "Instagram"
//                );
//            }
//        });
//        fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                shareImageSocialApp(
//                        "com.facebook.katana",
//                        "Facebook"
//                );
//            }
//        });
//        twitter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                shareImageSocialApp(
//                        "com.twitter.android",
//                        "Twitter"
//                );
//            }
//        });
//        more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                share.putExtra(
//                        "android.intent.extra.STREAM",
//                        FileProvider.getUriForFile(
//                                getApplicationContext(),
//                                BuildConfig.APPLICATION_ID + ".provider",
//                                saved_file
//                        )
//                );
//                share.setType("image/*");
//                startActivity(Intent.createChooser(share, "Share Image"));
//
//            }
//        });
//        sharebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(
                        "android.intent.extra.STREAM",
                        FileProvider.getUriForFile(
                                getApplicationContext(),
                                BuildConfig.APPLICATION_ID + ".provider",
                                saved_file
                        )
                );
                share.setType("image/*");
                startActivity(Intent.createChooser(share, "Share Image"));

//            }
//        });

    }


    private void shareImageSocialApp(String pkg, String appName) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.putExtra(
                "android.intent.extra.STREAM",
                FileProvider.getUriForFile(
                        getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        saved_file
                )
        );
        share.setType("image/*");

        if (isPackageInstalled(pkg, this)) {
            share.setPackage(pkg);
            startActivity(Intent.createChooser(share, "Share Image"));
            return;
        }
        Toast.makeText(getApplicationContext(), "Please Install " + appName, Toast.LENGTH_LONG).show();

    }

    private boolean isPackageInstalled(String packagename, Context context) {
        try {
            context.getPackageManager().getPackageInfo(packagename, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}