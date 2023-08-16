package com.photo.editor.snapstudio.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photo.editor.snapstudio.Adapter.ViewAdapter;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    SpringDotsIndicator dot1;
    ViewAdapter viewAdapter;

    TextView skip, next, getstarted;

    @Override
    protected void onResume() {
        super.onResume();
        AdUtils.loadInitialInterstitialAds(this);
        AdUtils.loadAppOpenAds(this);
        AdUtils.loadInitialNativeList(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_onboarding);
        viewPager = findViewById(R.id.view_pager);
        dot1 = findViewById(R.id.dot1);
        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);
        getstarted = findViewById(R.id.getstarted);

        AdUtils.showNativeAd(OnboardingActivity.this, findViewById(R.id.native_ads), false, false, false);


        viewAdapter = new ViewAdapter(this);
        viewPager.setAdapter(viewAdapter);
        dot1.setViewPager(viewPager);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdUtils.showInterstitialAd(OnboardingActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {

                        startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));

                    }
                });
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdUtils.showInterstitialAd(OnboardingActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        if (getitem(0) < 3) {
                            viewPager.setCurrentItem(getitem(1), true);
                        }
                        if (viewPager.getCurrentItem() == 3) {
                            getstarted.setVisibility(View.VISIBLE);
                            next.setVisibility(View.GONE);
                            skip.setVisibility(View.INVISIBLE);

                        }
                    }
                });
            }
        });

        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(OnboardingActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));

                    }
                });
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 3) {
                    getstarted.setVisibility(View.GONE);
                    next.setVisibility(View.VISIBLE);
                    skip.setVisibility(View.VISIBLE);

                } else {
                    getstarted.setVisibility(View.VISIBLE);
                    next.setVisibility(View.GONE);
                    skip.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private int getitem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        exitDialog();
    }

    private void exitDialog() {

        Dialog dialog = new Dialog(OnboardingActivity.this, R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(this);

        View lay = inflater.inflate(R.layout.exit_dialog, null);
        TextView goback, exit, rate_txt, do_you;
        ImageView rateUs;
        RelativeLayout exit_bg;
        goback = lay.findViewById(R.id.goback);
        exit = lay.findViewById(R.id.exit);
        rateUs = lay.findViewById(R.id.rate_us);
        rate_txt = lay.findViewById(R.id.rate_txt);

        LinearGradient shader = new
                LinearGradient(0f, 0f, 0f, rate_txt.getTextSize(), Color.parseColor("#DD81FF"), Color.parseColor("#1238FF"), Shader.TileMode.CLAMP);
        rate_txt.getPaint().setShader(shader);
//        if (isDarkTheme) {
//            exit_bg.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
//            exit.setTextColor(getResources().getColor(R.color.blacktwo));
//            do_you.setTextColor(getResources().getColor(R.color.blacktwo));
//            rate_txt.setTextColor(getResources().getColor(R.color.blacktwo));
//        } else {
//            exit_bg.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
//            exit.setTextColor(getResources().getColor(R.color.white));
//            do_you.setTextColor(getResources().getColor(R.color.white));
//            rate_txt.setTextColor(getResources().getColor(R.color.white));
//        }
        dialog.setContentView(lay);
        AdUtils.showNativeAd(OnboardingActivity.this, lay.findViewById(R.id.native_ad), true, false, true);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finishAffinity();
            }
        });

        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                rateApp();
            }
        });

        dialog.show();

    }

    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 33) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

}