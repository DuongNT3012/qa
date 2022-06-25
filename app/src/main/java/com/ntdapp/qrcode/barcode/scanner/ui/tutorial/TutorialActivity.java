package com.ntdapp.qrcode.barcode.scanner.ui.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admod;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.ntdapp.qrcode.barcode.scanner.Constant;
import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.ui.home.HomeActivity;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class TutorialActivity extends AppCompatActivity {

    ArrayList<HelpGuidModel> mHelpGuid;
    ViewPager2 viewPager2;
    TutorialAdapter tutorialAdapter;
    CircleIndicator3 circleIndicator;
    AppCompatButton btnNext;
    TextView tvSkip;
    private LinearLayout banner;
    private InterstitialAd mInterstitialTutorial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Ads Inter
        if (Constant.REMOTE_INTER_TUTORIAL) {
            if (mInterstitialTutorial == null) {
                loadInterTutorial();
            }
        }

        viewPager2 = findViewById(R.id.view_pager2);
        circleIndicator = findViewById(R.id.circle_indicator);
        btnNext = findViewById(R.id.btn_next);
        tvSkip = findViewById(R.id.tv_skip);

        mHelpGuid = new ArrayList<>();

        mHelpGuid.add(new HelpGuidModel(R.drawable.img_guide1, "Scan QRCode, BarCode with your camera"));
        mHelpGuid.add(new HelpGuidModel(R.drawable.img_guide2, "Enter the content, select the type of code you want to generate"));
        mHelpGuid.add(new HelpGuidModel(R.drawable.img_guide3, "Click here to view your scan history"));

        tutorialAdapter = new TutorialAdapter(mHelpGuid, this);

        viewPager2.setAdapter(tutorialAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_ALWAYS);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(100));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.8f + r * 0.2f);
                float absPosition = Math.abs(position);
                // alpha from MIN_ALPHA to MAX_ALPHA
                page.setAlpha(1.0f - (1.0f - 0.3f) * absPosition);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                    case 1:
                        btnNext.setText("Next");
                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewPager2.setCurrentItem(position + 1);
                            }
                        });
                        break;
                    case 2:
                        btnNext.setText("Get started");
                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Admod.getInstance().showInterAds(TutorialActivity.this, mInterstitialTutorial, new InterCallback() {
                                    @Override
                                    public void onAdClosed() {
                                        startActivity(new Intent(TutorialActivity.this, HomeActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onAdFailedToLoad(LoadAdError i) {
                                        onAdClosed();
                                    }
                                });
                                /*startActivity(new Intent(TutorialActivity.this, HomeActivity.class));
                                finish();*/
                            }
                        });
                        break;
                }
            }
        });
        circleIndicator.setViewPager(viewPager2);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Admod.getInstance().showInterAds(TutorialActivity.this, mInterstitialTutorial, new InterCallback() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(TutorialActivity.this, HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError i) {
                        onAdClosed();
                    }
                });
                /*startActivity(new Intent(TutorialActivity.this, HomeActivity.class));
                finish();*/
            }
        });
    }

    private void loadInterTutorial() {
        Admod.getInstance().loadInterAds(this, getString(R.string.inter_tutorial), new InterCallback() {
            @Override
            public void onInterstitialLoad(InterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                mInterstitialTutorial = interstitialAd;
            }
        });
    }
}