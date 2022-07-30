package com.ntdapp.qrcode.barcode.scanner.ui.language;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ads.AppIronSource;
import com.example.ads.funtion.AdCallback;
import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.ui.SystemUtil;
import com.ntdapp.qrcode.barcode.scanner.ui.language.adapter.LanguageAdapterMain;
import com.ntdapp.qrcode.barcode.scanner.ui.tutorial.TutorialActivity;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity implements IClickLanguage {

    private LanguageAdapterMain adapter;
    private LanguageModel model = new LanguageModel();
    private SharedPreferences sharedPreferences = null;
    private FrameLayout fr_ads;
    private RecyclerView rcl_language;
    private AppCompatButton iv_done;

    @Override
    public void onStart() {
        super.onStart();
        AppIronSource.getInstance().loadBanner(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        // load and show ads native language
        sharedPreferences = getSharedPreferences("MY_PRE", MODE_PRIVATE);

        fr_ads = findViewById(R.id.fr_ads);
        rcl_language = findViewById(R.id.rcl_language);
        iv_done = findViewById(R.id.iv_done);

        /*if (Constant.REMOTE_NATIVE_LANGUAGE) {
            fr_ads.setVisibility(View.VISIBLE);
            loadNativeLanguage();
        }*/

        if (!AppIronSource.getInstance().isInterstitialReady()) {
            AppIronSource.getInstance().loadInterstitial(this, new AdCallback());
        }

        adapter = new LanguageAdapterMain(this, setLanguageDefault(), this);
        rcl_language.setAdapter(adapter);

        iv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check get into language activity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("languageActivity", true);
                editor.apply();
                if (model != null) {
                    SystemUtil.setPreLanguage(LanguageActivity.this, model.getIsoLanguage());
                }
                SystemUtil.setLocale(LanguageActivity.this);
                startNextActivity();
            }
        });
    }

    @Override
    public void onClick(LanguageModel data) {
        adapter.setSelectLanguage(data);
        model = data;
    }

    private List<LanguageModel> setLanguageDefault() {
        ArrayList<LanguageModel> lists = new ArrayList();
        String key = SystemUtil.getPreLanguage(this);
        lists.add(new LanguageModel("English", "en", false, R.drawable.ic_english_flag));
        lists.add(new LanguageModel("French", "fr", false, R.drawable.ic_french_flag));
        lists.add(new LanguageModel("Portuguese", "pt", false, R.drawable.ic_portuguese_flag));
        lists.add(new LanguageModel("Spanish", "es", false, R.drawable.ic_spanish));
        lists.add(new LanguageModel("German", "de", false, R.drawable.ic_german_flag));
        Log.e("", "setLanguageDefault: $key");
        for (int i = 0; i < lists.size(); i++) {
            if (!sharedPreferences.getBoolean("nativeLanguage", false)) {
                if (key.equals(lists.get(i).getIsoLanguage())) {
                    LanguageModel data = lists.get(i);
                    data.setCheck(true);
                    lists.remove(lists.get(i));
                    lists.add(0, data);
                    break;
                }
            } else {
                if (key.equals(lists.get(i).getIsoLanguage())) {
                    lists.get(i).setCheck(true);
                }
            }
        }
        return lists;
    }

    private void startNextActivity() {
        if (AppIronSource.getInstance().isInterstitialReady()) {
            AppIronSource.getInstance().showInterstitial(LanguageActivity.this, new AdCallback() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Intent intent = new Intent(LanguageActivity.this, TutorialActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(LanguageActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /*private void loadNativeLanguage() {
        try {
            Admod.getInstance()
                    .loadNativeAd(this, getString(R.string.native_language), new AdCallback() {
                        @Override
                        public void onUnifiedNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            super.onUnifiedNativeAdLoaded(nativeAd);
                            NativeAdView adView = (NativeAdView) LayoutInflater.from(LanguageActivity.this).inflate(R.layout.layout_native_language, null);
                            fr_ads.removeAllViews();
                            fr_ads.addView(adView);
                            Admod.getInstance().populateUnifiedNativeAdView(nativeAd, adView);
                        }

                        @Override
                        public void onAdFailedToLoad(@Nullable LoadAdError i) {
                            super.onAdFailedToLoad(i);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            fr_ads.removeAllViews();
        }

        if (!Constant.haveNetworkConnection(LanguageActivity.this)) {
            fr_ads.removeAllViews();
        }
    }*/
}
