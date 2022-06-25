package com.ntdapp.qrcode.barcode.scanner;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class Constant {
    public static String email = "nguyenduong30121999@gmail.com";
    public static String subject = "Feedback QR Code";
    public static boolean checkResumeGallery = false;
    public static boolean REMOTE_RESUME = true;
    public static boolean REMOTE_INTER_SPLASH = true;
    public static boolean REMOTE_INTER_SCAN_RESULT = true;
    public static boolean REMOTE_INTER_GENERATE = true;
    public static boolean REMOTE_INTER_TUTORIAL = true;
    public static boolean REMOTE_NATIVE_GENERATE = true;
    public static boolean REMOTE_NATIVE_GENERATED_CODE = true;
    public static boolean REMOTE_NATIVE_SCAN_RESULT = true;
    public static boolean REMOTE_NATIVE_SETTING = true;
    public static boolean REMOTE_NATIVE_EXIT = true;
    public static boolean REMOTE_BANNER_ALL = true;

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void initRemoteConfig(OnCompleteListener<Boolean> listener) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.reset();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(listener);
    }

    public static boolean getRemoteConfigBoolean(String adUnitId){
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        return mFirebaseRemoteConfig.getBoolean(adUnitId);
    }
}
