package com.ntdapp.qrcode.barcode.scanner;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.ads.control.ads.AppOpenManager;
import com.ads.control.ads.application.AdsApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        Constant.initRemoteConfig(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    Constant.REMOTE_INTER_SPLASH = Constant.getRemoteConfigBoolean("inter_splash");
                    Constant.REMOTE_RESUME = Constant.getRemoteConfigBoolean("ad_app_open_resume");
                    if (enableAdsResume()) {
                        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
                    }
                }
            }
        });
    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public List<String> getListTestDeviceId() {
        return null;
    }

    @Override
    public String getOpenAppAdId() {
        return getString(R.string.ad_app_open_resume);
    }

    @Override
    public Boolean buildDebug() {
        return true;
    }

    @Override
    public boolean enableAdjust() {
        return false;
    }

    @Override
    public String getAdjustToken() {
        return null;
    }
}
