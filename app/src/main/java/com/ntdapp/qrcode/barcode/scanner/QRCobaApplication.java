package com.ntdapp.qrcode.barcode.scanner;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.amazic.ads.util.AdsApplication;
import com.amazic.ads.util.AppOpenManager;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.SharedPrefUtil;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.database.DatabaseUtil;
import com.ntdapp.qrcode.barcode.scanner.ui.splash.SplashActivity;

import java.util.List;

public class QRCobaApplication extends AdsApplication {

    private static QRCobaApplication sInstance;

    public static Context getContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        SharedPrefUtil.init(getApplicationContext());
        DatabaseUtil.init(getApplicationContext());

        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
    }

    @Override
    public boolean enableAdsResume() {
        return false;
    }

    @Override
    public List<String> getListTestDeviceId() {
        return null;
    }

    @Override
    public String getResumeAdId() {
        return getString(R.string.ad_app_open_resume);
    }
}
