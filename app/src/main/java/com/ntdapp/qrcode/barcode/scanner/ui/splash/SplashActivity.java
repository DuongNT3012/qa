package com.ntdapp.qrcode.barcode.scanner.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admod;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ntdapp.qrcode.barcode.scanner.Constant;
import com.ntdapp.qrcode.barcode.scanner.ui.home.HomeActivity;

import com.ntdapp.qrcode.barcode.scanner.R;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setBackgroundDrawable(null);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //

        Admod.getInstance().setOpenActivityAfterShowInterAds(checkPermission(getPermission()));

        initializeViews();
        animateLogo();
        if (Constant.haveNetworkConnection(this)) {
            Admod.getInstance().loadSplashInterAds(SplashActivity.this, getString(R.string.inter_splash), 25000, 5000, new InterCallback() {
                @Override
                public void onAdClosed() {
                    goToMainPage();
                }

                @Override
                public void onAdFailedToLoad(LoadAdError i) {
                    goToMainPage();
                }
            });
        } else {
            new Handler().postDelayed(() -> {
                goToMainPage();
            }, SPLASH_DELAY);
        }
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
        //new Handler().postDelayed(() -> {
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
        //}, SPLASH_DELAY);
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
