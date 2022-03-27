package com.qrcode.barcode.barcodescanner.qrcodereader.ui.home;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qrcode.barcode.barcodescanner.qrcodereader.Constant;
import com.qrcode.barcode.barcodescanner.qrcodereader.RatingDialog;
import com.qrcode.barcode.barcodescanner.qrcodereader.SharePrefUtils;
import com.qrcode.barcode.barcodescanner.qrcodereader.VAppUtility;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.PermissionUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.generate.GenerateFragment;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.history.HistoryFragment;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.scan.ScanFragment;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.settings.SettingsActivity;

import com.qrcode.barcode.barcodescanner.qrcodereader.R;
import com.qrcode.barcode.barcodescanner.qrcodereader.databinding.ActivityHomeBinding;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomeBinding mBinding;
    private Menu mToolbarMenu;

    private Dialog dialog;
    NativeAd nativeAd;
    private ReviewManager manager;
    private  ReviewInfo reviewInfo;

    private FirebaseAnalytics mFirebaseAnalytics;

    public Menu getToolbarMenu() {
        return mToolbarMenu;
    }

    public void setToolbarMenu(Menu toolbarMenu) {
        mToolbarMenu = toolbarMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

//        getWindow().setBackgroundDrawable(null);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //

        setListeners();
        initializeToolbar();
        initializeBottomBar();
        setting();
//        checkInternetConnection();
//        playAd();

        //add ads banner
        addAdsBanner();
        loadDigLogNativeAds();
        //
    }
    private void setting(){
        mBinding.imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!SharePrefUtils.isRated(this)) {
            int count = SharePrefUtils.getCountOpenApp(this);
            Log.e("TAG", "onBackPressed: "+ count );
            if ( count == 2 || count == 4 || count == 6 || count == 8 || count == 10) {
                showRateDialog();
                return;
            } else {
                showDialogNativeAds();
            }
        } else if (SharePrefUtils.isRated(this)) {
            showDialogNativeAds();
        }
    }

    //ads native close app
    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));

        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));


        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);

        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);


    }
    private void loadNativeAds(Dialog dialog) {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_native_exit));
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout = dialog.findViewById(R.id.ad_frame);
                NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_ads_exit_app, null);

                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);

            }
        }).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }
    private  void loadDigLogNativeAds(){
        dialog = new Dialog(HomeActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_exit_app, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int w = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        int h = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(w, h);
        loadNativeAds(dialog);
    }
    private void showDialogNativeAds() {
        TextView btnOK= dialog.findViewById(R.id.btnExit);
        TextView btnCancel= dialog.findViewById(R.id.btnNo);

        btnOK.setOnClickListener(v -> {
            SharePrefUtils.increaseCountOpenApp(HomeActivity.this);
            finish();
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
        }
    };

    private void showRateDialog(){
        RatingDialog ratingDialog = new RatingDialog(this);
        ratingDialog.init(HomeActivity.this, new RatingDialog.OnPress() {
            @Override
            public void send() {
                ratingDialog.dismiss();
                String uriText = "mailto:" + Constant.email+
                        "?subject=" + Constant.subject +
                        "&body=" +"Rate : "+ratingDialog.getRating()+"\nContent: "+ ratingDialog.getNewName();
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(sendIntent, "Send Email"));
                    SharePrefUtils.forceRated(HomeActivity.this);
                    finish();
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(HomeActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void rating() {
              /* startActivity(
                       new Intent(
                               Intent.ACTION_VIEW,
                               Uri.parse("https://play.google.com/store/apps/details?id="+ Constant.packageName)
                       )
               );*/
                manager = ReviewManagerFactory.create(HomeActivity.this);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                    @Override
                    public void onComplete(Task<ReviewInfo> task) {
                        if(task.isSuccessful()){
                            reviewInfo=task.getResult();
                            Task<Void> flow = manager.launchReviewFlow(HomeActivity.this,reviewInfo);
                            flow.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    SharePrefUtils.forceRated(HomeActivity.this);
                                    ratingDialog.dismiss();
                                    finishAffinity();
                                }
                            });
                        }else{
                            ratingDialog.dismiss();
                            finishAffinity();
                        }
                    }
                });

            }
            @Override
            public void cancel() {
                SharePrefUtils.increaseCountOpenApp(HomeActivity.this);
                finishAffinity();
                System.exit(0);
            }

            @Override
            public void later() {
                SharePrefUtils.increaseCountOpenApp(HomeActivity.this);
                finishAffinity();
                System.exit(0);
            }

        });
        ratingDialog.show();
    }
    //

    //add ads banner main
    private void addAdsBanner(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLoaded() {

                mBinding.adView.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(@NonNull AdValue adValue) {
                        VAppUtility.logAdAdmobValue(adValue,
                                mBinding.adView.getAdUnitId(),
                                mBinding.adView.getResponseInfo().getMediationAdapterClassName(),
                                mFirebaseAnalytics);
                    }
                });
            }

            @Override
            public void onAdClicked() {

            }
        });

        mBinding.adView.loadAd(adRequest);

    }
    //end

//    private void checkInternetConnection() {
//        CompositeDisposable disposable = new CompositeDisposable();
//        disposable.add(ReactiveNetwork
//                .observeNetworkConnectivity(this)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(connectivity -> {
//                    if (connectivity.state() == NetworkInfo.State.CONNECTED) {
//                        mBinding.adView.setVisibility(View.VISIBLE);
//                    } else {
//                        mBinding.adView.setVisibility(View.GONE);
//                    }
//
//                }, throwable -> {
//                    Toast.makeText(this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
//                }));
//    }
//
//    private void playAd() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mBinding.adView.loadAd(adRequest);
//        mBinding.adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                mBinding.adView.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAdOpened() {
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//            }
//
//            @Override
//            public void onAdClosed() {
//            }
//        });
//    }

    private void initializeToolbar() {
        setSupportActionBar(mBinding.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        setToolbarMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setListeners() {
        mBinding.textViewGenerate.setOnClickListener(this);
        mBinding.textViewScan.setOnClickListener(this);
        mBinding.textViewHistory.setOnClickListener(this);

        mBinding.imageViewGenerate.setOnClickListener(this);
        mBinding.imageViewScan.setOnClickListener(this);
        mBinding.imageViewHistory.setOnClickListener(this);

        mBinding.constraintLayoutGenerateContainer.setOnClickListener(this);
        mBinding.constraintLayoutScanContainer.setOnClickListener(this);
        mBinding.constraintLayoutHistoryContainer.setOnClickListener(this);
    }

    private void initializeBottomBar() {
        clickOnScan();
    }

    private void clickOnGenerate() {
        mBinding.textViewGenerate.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_selected));

        mBinding.textViewScan.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_normal));

        mBinding.textViewHistory.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_normal));

        mBinding.imageViewGenerate.setVisibility(View.INVISIBLE);
        mBinding.imageViewGenerateActive.setVisibility(View.VISIBLE);

        mBinding.imageViewScan.setVisibility(View.VISIBLE);
        mBinding.imageViewScanActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewHistory.setVisibility(View.VISIBLE);
        mBinding.imageViewHistoryActive.setVisibility(View.INVISIBLE);

        setToolbarTitle(getString(R.string.toolbar_title_generate));
        showFragment(GenerateFragment.newInstance());
    }

    private void clickOnScan() {
        if (PermissionUtil.on().requestPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {


            mBinding.textViewGenerate.setTextColor(
                    ContextCompat.getColor(this, R.color.bottom_bar_normal));

            mBinding.textViewScan.setTextColor(
                    ContextCompat.getColor(this, R.color.bottom_bar_selected));

            mBinding.textViewHistory.setTextColor(
                    ContextCompat.getColor(this, R.color.bottom_bar_normal));

            mBinding.imageViewGenerate.setVisibility(View.VISIBLE);
            mBinding.imageViewGenerateActive.setVisibility(View.INVISIBLE);

            mBinding.imageViewScan.setVisibility(View.INVISIBLE);
            mBinding.imageViewScanActive.setVisibility(View.VISIBLE);

            mBinding.imageViewHistory.setVisibility(View.VISIBLE);
            mBinding.imageViewHistoryActive.setVisibility(View.INVISIBLE);

            setToolbarTitle(getString(R.string.toolbar_title_scan));

        /*IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setBeepEnabled(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.PLAY_SOUND));
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a barcode");
        integrator.initiateScan();

        *//*
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
         */
            showFragment(ScanFragment.newInstance());
        }
    }

    private void clickOnHistory() {
        mBinding.textViewGenerate.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_normal));

        mBinding.textViewScan.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_normal));

        mBinding.textViewHistory.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_selected));

        mBinding.imageViewGenerate.setVisibility(View.VISIBLE);
        mBinding.imageViewGenerateActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewScan.setVisibility(View.VISIBLE);
        mBinding.imageViewScanActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewHistory.setVisibility(View.INVISIBLE);
        mBinding.imageViewHistoryActive.setVisibility(View.VISIBLE);

        setToolbarTitle(getString(R.string.toolbar_title_history));
        showFragment(HistoryFragment.newInstance());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_generate:
            case R.id.text_view_generate:
            case R.id.constraint_layout_generate_container:
                clickOnGenerate();
                break;

            case R.id.image_view_scan:
            case R.id.text_view_scan:
            case R.id.constraint_layout_scan_container:
                clickOnScan();
                break;

            case R.id.image_view_history:
            case R.id.text_view_history:
            case R.id.constraint_layout_history_container:
                clickOnHistory();
                break;
        }
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.coordinator_layout_fragment_container, fragment,
                fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtil.REQUEST_CODE_PERMISSION_DEFAULT) {
            boolean isAllowed = true;

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllowed = false;
                }
            }

            if (isAllowed) {
                clickOnScan();
            }
        }
    }

  /*  public void hideAdMob()
    {
        if (mBinding.adView.isShown())
            mBinding.adView.setVisibility(View.GONE);
    }

    public void showAdmob()
    {
        if (!mBinding.adView.isShown())
            mBinding.adView.setVisibility(View.VISIBLE);
    }*/
}
