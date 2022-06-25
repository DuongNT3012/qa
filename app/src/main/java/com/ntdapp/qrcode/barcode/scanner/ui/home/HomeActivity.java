package com.ntdapp.qrcode.barcode.scanner.ui.home;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admod;
import com.amazic.ads.util.AppOpenManager;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ntdapp.qrcode.barcode.scanner.Constant;
import com.ntdapp.qrcode.barcode.scanner.RatingDialog;
import com.ntdapp.qrcode.barcode.scanner.SharePrefUtils;
import com.ntdapp.qrcode.barcode.scanner.ui.generate.GenerateFragment;
import com.ntdapp.qrcode.barcode.scanner.ui.history.HistoryFragment;
import com.ntdapp.qrcode.barcode.scanner.ui.scan.ScanFragment;
import com.ntdapp.qrcode.barcode.scanner.ui.settings.SettingsActivity;

import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomeBinding mBinding;
    private Menu mToolbarMenu;

    private Dialog dialog;
    NativeAd nativeAd;
    private ReviewManager manager;
    private ReviewInfo reviewInfo;

    private FirebaseAnalytics mFirebaseAnalytics;
    private AlertDialog alertDialog;
    private boolean checkAdsResume = false;

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
        //banner
        if (Constant.REMOTE_BANNER_ALL) {
            Admod.getInstance().loadBanner(HomeActivity.this, getResources().getString(R.string.ad_banner_all));
        }
        alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialogPermission).create();
        alertDialog.setTitle("Grant Permission");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Please grant all permissions to access additional functionality.");
        alertDialog.setButton(-1, (CharSequence) "Go to setting", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialogInterface, int i) {
                checkAdsResume = true;
                AppOpenManager.getInstance().disableAppResume();
                alertDialog.dismiss();
                requestPermissions(new String[]{Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1112);
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        //Permission
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1111);
        }

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //

        setListeners();
        initializeToolbar();
        initializeBottomBar();
        setting();

        loadDigLogNativeAds();
        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        if (checkAdsResume) {
            AppOpenManager.getInstance().enableAppResume();
        }
        //
        if (Constant.checkResumeGallery) {
            AppOpenManager.getInstance().enableAppResumeWithActivity(HomeActivity.class);
        }
    }

    private void setting() {
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
            Log.e("TAG", "onBackPressed: " + count);
            if (count == 2 || count == 4 || count == 6 || count == 8 || count == 10) {
                showRateDialog();
                return;
            } else {
                showDialogNativeAds();
            }
        } else if (SharePrefUtils.isRated(this)) {
            showDialogNativeAds();
        }
    }

    private void loadDigLogNativeAds() {
        dialog = new Dialog(HomeActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_exit_app, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int w = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        int h = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(w, h);
    }

    private void showDialogNativeAds() {
        TextView btnOK = dialog.findViewById(R.id.btnExit);
        TextView btnCancel = dialog.findViewById(R.id.btnNo);
        FrameLayout flNative = dialog.findViewById(R.id.fl_native);

        // load ads native exit
        if (Constant.REMOTE_NATIVE_EXIT) {
            try {
                Admod.getInstance().loadNativeAd(HomeActivity.this, getString(R.string.ad_native_exit), new NativeCallback() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeAdView adView = (NativeAdView) LayoutInflater.from(HomeActivity.this).inflate(R.layout.ads_native_large, null);
                        flNative.removeAllViews();
                        flNative.addView(adView);
                        Admod.getInstance().pushAdsToViewCustom(nativeAd, adView);
                    }

                    @Override
                    public void onAdFailedToLoad() {
                        flNative.removeAllViews();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                flNative.removeAllViews();
            }
        } else {
            flNative.removeAllViews();
        }

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

    private void showRateDialog() {
        RatingDialog ratingDialog = new RatingDialog(this);
        ratingDialog.init(HomeActivity.this, new RatingDialog.OnPress() {
            @Override
            public void send() {
                ratingDialog.dismiss();
                String uriText = "mailto:" + Constant.email +
                        "?subject=" + Constant.subject +
                        "&body=" + "Rate : " + ratingDialog.getRating() + "\nContent: " + ratingDialog.getNewName();
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(sendIntent, "Send Email"));
                    SharePrefUtils.forceRated(HomeActivity.this);
                    finish();
                } catch (android.content.ActivityNotFoundException ex) {
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
                        if (task.isSuccessful()) {
                            reviewInfo = task.getResult();
                            Task<Void> flow = manager.launchReviewFlow(HomeActivity.this, reviewInfo);
                            flow.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    SharePrefUtils.forceRated(HomeActivity.this);
                                    ratingDialog.dismiss();
                                    finishAffinity();
                                }
                            });
                        } else {
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
    //end

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
                //mBinding.banner.setVisibility(View.GONE);
                break;

            case R.id.image_view_scan:
            case R.id.text_view_scan:
            case R.id.constraint_layout_scan_container:
                clickOnScan();
                //mBinding.banner.setVisibility(View.VISIBLE);
                break;

            case R.id.image_view_history:
            case R.id.text_view_history:
            case R.id.constraint_layout_history_container:
                clickOnHistory();
                //mBinding.banner.setVisibility(View.VISIBLE);
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
        int count = 0;
        if (requestCode == 1111 || requestCode == 1112) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    count++;
                }
            }
            if (count > 0) {
                Toast.makeText(getBaseContext(), "Permission is denied", Toast.LENGTH_SHORT).show();
                alertDialog.show();
            }
        }
    }
}
