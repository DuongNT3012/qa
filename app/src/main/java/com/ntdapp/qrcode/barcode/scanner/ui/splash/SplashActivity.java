package com.ntdapp.qrcode.barcode.scanner.ui.splash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.Admod;
import com.ads.control.funtion.AdCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ntdapp.qrcode.barcode.scanner.Constant;
import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.ui.language.LanguageActivity;
import com.ntdapp.qrcode.barcode.scanner.ui.tutorial.TutorialActivity;

public class SplashActivity extends AppCompatActivity {

    /**
     * Constants
     */
    private final int SPLASH_DELAY = 3000;

    /**
     * Fields
     */
    private ImageView mImageViewLogo;
    private TextView mtvSplash;

    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences sharedPreferences;
    private boolean languageActivity = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setBackgroundDrawable(null);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //

        Constant.initRemoteConfig(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    Constant.REMOTE_INTER_SCAN_RESULT = Constant.getRemoteConfigBoolean("inter_scan_result");
                    Constant.REMOTE_INTER_GENERATE = Constant.getRemoteConfigBoolean("inter_generate");
                    Constant.REMOTE_INTER_TUTORIAL = Constant.getRemoteConfigBoolean("inter_tutorial");
                    Constant.REMOTE_NATIVE_GENERATE = Constant.getRemoteConfigBoolean("ad_native_generate");
                    Constant.REMOTE_NATIVE_GENERATED_CODE = Constant.getRemoteConfigBoolean("ad_native_generated_code");
                    Constant.REMOTE_NATIVE_SCAN_RESULT = Constant.getRemoteConfigBoolean("ad_native_scan_result");
                    Constant.REMOTE_NATIVE_SETTING = Constant.getRemoteConfigBoolean("ad_native_setting");
                    Constant.REMOTE_NATIVE_EXIT = Constant.getRemoteConfigBoolean("ad_native_exit");
                    Constant.REMOTE_BANNER_ALL = Constant.getRemoteConfigBoolean("ad_banner_all");
                    Constant.REMOTE_NATIVE_LANGUAGE = Constant.getRemoteConfigBoolean("native_language");
                }
            }
        });

        Admod.getInstance().setOpenActivityAfterShowInterAds(checkPermission(getPermission()));

        initializeViews();
        animateLogo();
        if (Constant.REMOTE_INTER_SPLASH) {
            if (Constant.haveNetworkConnection(this)) {
                Admod.getInstance().loadSplashInterstitalAds(SplashActivity.this, getString(R.string.inter_splash), 25000, 5000, new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        goToMainPage();
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        goToMainPage();
                    }
                });
            } else {
                new Handler().postDelayed(() -> {
                    goToMainPage();
                }, SPLASH_DELAY);
            }
        } else {
            new Handler().postDelayed(() -> {
                goToMainPage();
            }, SPLASH_DELAY);
        }
        /*new Handler().postDelayed(() -> {
            goToMainPage();
        }, SPLASH_DELAY);*/
        sharedPreferences = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE);
        languageActivity = sharedPreferences.getBoolean("languageActivity", false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkPermission(String[] per) {
        for (String s : per) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public String[] getPermission() {
        return new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    /**
     * This method initializes the views
     */
    private void initializeViews() {
        mImageViewLogo = findViewById(R.id.image_view_logo);
        mtvSplash = findViewById(R.id.tv_splash);
    }

    /**
     * This method takes user to the main page
     */
    private void goToMainPage() {
        if (languageActivity) {
            startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
            finish();
        }
    }

    /**
     * This method animates the logo
     */
    private void animateLogo() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_without_duration);
        fadeInAnimation.setDuration(SPLASH_DELAY);

        mImageViewLogo.startAnimation(fadeInAnimation);
        mtvSplash.startAnimation(fadeInAnimation);
    }
}
