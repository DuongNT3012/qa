package com.qrcode.barcode.barcodescanner.qrcodereader;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

//import com.google.android.gms.ads.MobileAds;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.SharedPrefUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.database.DatabaseUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.splash.SplashActivity;
import com.vapp.admoblibrary.ads.AppOpenManager;

//import com.qrcode.barcode.barcodescanner.qrcodereader.R;

public class QRCobaApplication extends MultiDexApplication {

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
//        MobileAds.initialize(this, getString(R.string.admob_app_id));

        //ads resume
        AppOpenManager.getInstance().init(this,getString(R.string.ad_appopen_resume));
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
    }
}
