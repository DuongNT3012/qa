package com.qrcode.barcode.barcodescanner.qrcodereader.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qrcode.barcode.barcodescanner.qrcodereader.VAppUtility;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.home.HomeActivity;

import com.qrcode.barcode.barcodescanner.qrcodereader.R;

public class SplashActivity extends AppCompatActivity {

    /**
     * Constants
     */
    private final int SPLASH_DELAY = 5000;

    /**
     * Fields
     */
    private ImageView mImageViewLogo;
    private TextView mtvSplash;

    private InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setBackgroundDrawable(null);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //

        //ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                createPersonalizedAd();
            }
        });
        //

        initializeViews();
        animateLogo();
        goToMainPage();
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
        new Handler().postDelayed(() -> {
            /*startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();*/
            if (mInterstitialAd != null) {

                mInterstitialAd.show(SplashActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }, SPLASH_DELAY);
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

    //add ads inter splash
    private void createPersonalizedAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        createInterstitialAd(adRequest);
    }
    private void createInterstitialAd(AdRequest adRequest){
        InterstitialAd.load(this,getString(R.string.ad_inter_splash), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        //log event
                        mInterstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                VAppUtility.logAdAdmobValue(adValue,
                                        mInterstitialAd.getAdUnitId(),
                                        mInterstitialAd.getResponseInfo().getMediationAdapterClassName(),
                                        mFirebaseAnalytics);
                            }
                        });
                        //

                        Log.i("TAG", "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                finish();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
    //
}
