package com.ntdapp.qrcode.barcode.scanner;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

//import com.google.android.gms.ads.MobileAds;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.SharedPrefUtil;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.database.DatabaseUtil;

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
    }
}
