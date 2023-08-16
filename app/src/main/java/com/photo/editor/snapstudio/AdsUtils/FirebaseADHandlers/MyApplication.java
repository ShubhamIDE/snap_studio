package com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers;

import static com.photo.editor.snapstudio.AdsUtils.Utils.Constants.isNull;

import android.app.Application;

import com.photo.editor.snapstudio.AdsUtils.PreferencesManager.AppPreferences;
import com.photo.editor.snapstudio.AdsUtils.Utils.Constants;
import com.photo.editor.snapstudio.AdsUtils.Utils.Global;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends Application {

    private static MyApplication instance;

    public static synchronized MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        AppPreferences appPreferencesManger = new AppPreferences(this);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.ADSJSON);
        FirebaseApp.initializeApp(this);
        Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());

        if (Constants.adsJsonPOJO != null && !isNull(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue())) {
            Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());
            Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
        } else {
            FirebaseUtils.initiateAndStoreFirebaseRemoteConfig(this, adsJsonPOJO -> {
                appPreferencesManger.setAdsModel(adsJsonPOJO);
                Constants.adsJsonPOJO = adsJsonPOJO;
                Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
            });
        }

        MobileAds.initialize(this, initializationStatus -> {});
        new AppOpenAds(this);

    }

}
