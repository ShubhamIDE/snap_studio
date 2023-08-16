package com.photo.editor.snapstudio.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdsJsonPOJO;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.FirebaseUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.AdsUtils.PreferencesManager.AppPreferences;
import com.photo.editor.snapstudio.AdsUtils.Utils.Constants;
import com.photo.editor.snapstudio.AdsUtils.Utils.Global;
import com.photo.editor.snapstudio.R;

public class SplashActivity extends AppCompatActivity {

    Thread timer;
    FirebaseAuth auth;
    String userId = null;

    Activity splashActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();

        splashActivity = SplashActivity.this;
        AppPreferences appPreferencesManger = new AppPreferences(this);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.ADSJSON);

        Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());

        if (Constants.adsJsonPOJO != null && !Global.isEmptyStr(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue())) {
            AdUtils.loadInitialInterstitialAds(SplashActivity.this);
            AdUtils.loadAppOpenAds(SplashActivity.this);
            AdUtils.loadCollapsibleAdsList(SplashActivity.this);
            AdUtils.loadInitialNativeList(SplashActivity.this);
            Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());
            Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
            AdUtils.showAdIfAvailable(splashActivity, new AppInterfaces.AppOpenADInterface() {
                @Override
                public void appOpenAdState(boolean state_load) {

                    nextActivity();

                }
            });

        } else {
            FirebaseUtils.initiateAndStoreFirebaseRemoteConfig(splashActivity, new AppInterfaces.AdDataInterface() {
                @Override
                public void getAdData(AdsJsonPOJO adsJsonPOJO) {
                    //Need to call this only once per
                    appPreferencesManger.setAdsModel(adsJsonPOJO);
                    Constants.adsJsonPOJO = adsJsonPOJO;
                    AdUtils.loadInitialInterstitialAds(SplashActivity.this);
                    AdUtils.loadAppOpenAds(SplashActivity.this);
                    AdUtils.loadCollapsibleAdsList(SplashActivity.this);
                    AdUtils.loadInitialNativeList(SplashActivity.this);
                    Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
                    AdUtils.showAdIfAvailable(splashActivity, new AppInterfaces.AppOpenADInterface() {
                        @Override
                        public void appOpenAdState(boolean state_load) {

                            nextActivity();
                        }
                    });

                }
            });
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        AdUtils.loadInitialInterstitialAds(this);
        AdUtils.loadAppOpenAds(this);
        AdUtils.loadInitialNativeList(this);
    }
    private void nextActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            Boolean isFirstRun = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isFirstRun", true);
            if (isFirstRun) {
//
                Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                startActivity(intent);
            }else{

                if (userId == null) {
//
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                    startActivity(intent);
                }
            }
//
            getSharedPreferences(getResources().getString(R.string.app_name),MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();

        }, 2000);
    }


   /* timer = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Boolean isFirstRun = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isFirstRun", true);
                    if (isFirstRun) {
//
                        Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                        startActivity(intent);
                    }else{

                        if (userId == null) {
//
                            Intent intent = new Intent(SplashActivity.this, SigninActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        }
                    }
//
                    getSharedPreferences(getResources().getString(R.string.app_name),MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();

                }
            }
        };
        timer.start();
    }*/
}