package com.photo.editor.snapstudio.AdsUtils.Utils;


import com.google.android.gms.ads.rewarded.RewardedAd;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdsJsonPOJO;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;

public class Constants {
    public static final String ADSJSON = "SNAP_STUDIO_ADS_JSON";
    public static final String IS_FIRST_RUN = "isFirstRun";
    public static String CURRENCY = "CURRENCY";
    public static String CURRENCY_STORED = "";
    public static boolean LIGHT_THEME = true;
    public static String PACKAGE_NAME = "com.photo.editor.snapstudio";
    public static AdsJsonPOJO adsJsonPOJO;
    public static int hitCounter = 0;
    public static ArrayList<InterstitialAd> InterstitialList = new ArrayList<>();
    public static ArrayList<AppOpenAd> AppOpenAdsList = new ArrayList<>();
    public static ArrayList<NativeAd> NativeAdsList = new ArrayList<>();
    public static ArrayList<RewardedAd> RewardedAdsList = new ArrayList<>();
    public static ArrayList<AdView> CollapsibleAdsList = new ArrayList<>();

    public static boolean isNull(String val) {
        return val == null || val.trim().equalsIgnoreCase("") || val.trim().equalsIgnoreCase("null") || val.trim() == "" || val.trim() == "null";
    }

}
