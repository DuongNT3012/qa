package com.ntdapp.qrcode.barcode.scanner.ui.scanresult;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admod;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ntdapp.qrcode.barcode.scanner.helpers.constant.IntentKey;
import com.ntdapp.qrcode.barcode.scanner.helpers.constant.PreferenceKey;
import com.ntdapp.qrcode.barcode.scanner.helpers.model.Code;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.SharedPrefUtil;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.TimeUtil;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.database.DatabaseUtil;
import com.ntdapp.qrcode.barcode.scanner.ui.generatedcode.GeneratedCodeActivity;
import com.ntdapp.qrcode.barcode.scanner.ui.settings.SettingsActivity;

import java.io.File;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.databinding.ActivityScanResultBinding;

public class ScanResultActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeDisposable mCompositeDisposable;
    private ActivityScanResultBinding mBinding;
    private Menu mToolbarMenu;
    private Code mCurrentCode;
    private boolean mIsHistory, mIsPickedFromGallery;

    private FirebaseAnalytics mFirebaseAnalytics;

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        mCompositeDisposable = compositeDisposable;
    }

    public Code getCurrentCode() {
        return mCurrentCode;
    }

    public void setCurrentCode(Code currentCode) {
        mCurrentCode = currentCode;
    }

    public Menu getToolbarMenu() {
        return mToolbarMenu;
    }

    public void setToolbarMenu(Menu toolbarMenu) {
        mToolbarMenu = toolbarMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan_result);
        setCompositeDisposable(new CompositeDisposable());

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // load ads native generated code
        try {
            Admod.getInstance().loadNativeAd(ScanResultActivity.this, getString(R.string.ad_native_scan_result), new NativeCallback() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    NativeAdView adView = (NativeAdView) LayoutInflater.from(ScanResultActivity.this).inflate(R.layout.ads_native_large, null);
                    mBinding.flNative.removeAllViews();
                    mBinding.flNative.addView(adView);
                    Admod.getInstance().pushAdsToViewCustom(nativeAd, adView);
                }

                @Override
                public void onAdFailedToLoad() {
                    mBinding.flNative.removeAllViews();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mBinding.flNative.removeAllViews();
        }

        getWindow().setBackgroundDrawable(null);
        initializeToolbar();
        loadQRCode();
        setListeners();
        setBack();
        getSetting();
        //
    }

    private void getSetting() {
        mBinding.imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
    }

    private void setListeners() {
        mBinding.textViewOpenInBrowser.setOnClickListener(this);
        mBinding.imageViewShare.setOnClickListener(this);
    }

    private void loadQRCode() {
        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle != null && bundle.containsKey(IntentKey.MODEL)) {
                setCurrentCode(bundle.getParcelable(IntentKey.MODEL));
            }

            if (bundle != null && bundle.containsKey(IntentKey.IS_HISTORY)) {
                mIsHistory = bundle.getBoolean(IntentKey.IS_HISTORY);
            }

            if (bundle != null && bundle.containsKey(IntentKey.IS_PICKED_FROM_GALLERY)) {
                mIsPickedFromGallery = bundle.getBoolean(IntentKey.IS_PICKED_FROM_GALLERY);
            }
        }

        if (getCurrentCode() != null) {
            mBinding.textViewContent.setText(String.format(Locale.ENGLISH,
                    getString(R.string.content), getCurrentCode().getContent()));

            mBinding.textViewType.setText(String.format(Locale.ENGLISH, getString(R.string.code_type),
                    getResources().getStringArray(R.array.code_types)[getCurrentCode().getType()]));

            mBinding.textViewTime.setText(String.format(Locale.ENGLISH, getString(R.string.created_time),
                    TimeUtil.getFormattedDateString(getCurrentCode().getTimeStamp())));

//            mBinding.textViewOpenInBrowser.setEnabled(URLUtil.isValidUrl(getCurrentCode().getContent()));

            if (!TextUtils.isEmpty(getCurrentCode().getCodeImagePath())) {
                Glide.with(this)
                        .asBitmap()
                        .load(getCurrentCode().getCodeImagePath())
                        .into(mBinding.imageViewScannedCode);
            }

            if (SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.COPY_TO_CLIPBOARD)
                    && !mIsHistory) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText(
                            getString(R.string.scanned_qr_code_content),
                            getCurrentCode().getContent());
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(this, getString(R.string.copied_to_clipboard),
                            Toast.LENGTH_SHORT).show();
                }
            }

            if (SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.SAVE_HISTORY) && !mIsHistory) {
                getCompositeDisposable().add(DatabaseUtil.on().insertCode(getCurrentCode())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }));
            }
        }
    }

    private void initializeToolbar() {
        setSupportActionBar(mBinding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        setToolbarMenu(menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_open_in_browser:

                try {
                    if (getCurrentCode() != null
                            && URLUtil.isValidUrl(getCurrentCode().getContent())) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(getCurrentCode().getContent()));
                        startActivity(browserIntent);
                    } else {
                        Toast.makeText(ScanResultActivity.this, "incorrect syntax to open!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception exception){
                    Log.e("incorrect syntax", exception.toString());
                }
                break;

            case R.id.image_view_share:
                if (getCurrentCode() != null) {
                    shareCode(new File(getCurrentCode().getCodeImagePath()));
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCompositeDisposable().dispose();

        if (getCurrentCode() != null
                && !SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.SAVE_HISTORY)
                && !mIsHistory && !mIsPickedFromGallery) {
            new File(getCurrentCode().getCodeImagePath()).delete();
        }
    }

    private void shareCode(File codeImageFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider", codeImageFile));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(codeImageFile));
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_code_using)));
    }

    private void setBack() {
        mBinding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
