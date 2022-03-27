package com.qrcode.barcode.barcodescanner.qrcodereader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefUtils {
    private static SharedPreferences mSharePref;
    public static void init(Context context) {
        if (mSharePref == null) {
            mSharePref = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }
    public static boolean isRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("rated", false);
    }

    public static int getCountOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("counts", 1);
    }
    public static void increaseCountOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("counts", pre.getInt("counts", 1) + 1);
        editor.commit();
    }
    public static void forceRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("rated", true);
        editor.commit();
    }
}
