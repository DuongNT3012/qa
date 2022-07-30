package com.ntdapp.qrcode.barcode.scanner.ui.pickedfromgallery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.bumptech.glide.Glide;
import com.example.ads.AppIronSource;
import com.example.ads.funtion.AdCallback;
import com.ntdapp.qrcode.barcode.scanner.Constant;
import com.ntdapp.qrcode.barcode.scanner.helpers.constant.IntentKey;
import com.ntdapp.qrcode.barcode.scanner.helpers.model.Code;
import com.ntdapp.qrcode.barcode.scanner.ui.home.HomeActivity;
import com.ntdapp.qrcode.barcode.scanner.ui.scan_success.SuccessActivity;
import com.ntdapp.qrcode.barcode.scanner.ui.scanresult.ScanResultActivity;
import com.ntdapp.qrcode.barcode.scanner.ui.settings.SettingsActivity;

import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.databinding.ActivityPickedFromGalleryBinding;
import com.ntdapp.qrcode.barcode.scanner.ui.tutorial.TutorialActivity;

public class PickedFromGalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPickedFromGalleryBinding mBinding;
    private Code mCurrentCode;
    private Menu mToolbarMenu;

    public Menu getToolbarMenu() {
        return mToolbarMenu;
    }

    public void setToolbarMenu(Menu toolbarMenu) {
        mToolbarMenu = toolbarMenu;
    }

    public Code getCurrentCode() {
        return mCurrentCode;
    }

    public void setCurrentCode(Code currentCode) {
        mCurrentCode = currentCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_picked_from_gallery);

        if (!AppIronSource.getInstance().isInterstitialReady()) {
            AppIronSource.getInstance().loadInterstitial(this, new AdCallback());
        }

        initializeToolbar();
        loadQRCode();
        setListeners();
        setBack();
        getSetting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.checkResumeGallery) {
            //AppOpenManager.getInstance().enableAppResumeWithActivity(PickedFromGalleryActivity.class);
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

    private void loadQRCode() {
        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle != null && bundle.containsKey(IntentKey.MODEL)) {
                setCurrentCode(bundle.getParcelable(IntentKey.MODEL));
            }
        }

        if (getCurrentCode() != null) {
            if (!TextUtils.isEmpty(getCurrentCode().getCodeImagePath())) {
                Glide.with(this)
                        .asBitmap()
                        .load(getCurrentCode().getCodeImagePath())
                        .into(mBinding.imageViewScannedCode);
            }
        }
    }

    private void setListeners() {
        mBinding.textViewGetValue.setOnClickListener(this);
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
            case R.id.text_view_get_value:
                if (getCurrentCode() != null) {
                    if (AppIronSource.getInstance().isInterstitialReady()) {
                        AppIronSource.getInstance().showInterstitial(PickedFromGalleryActivity.this, new AdCallback() {
                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
                                Intent intent = new Intent(PickedFromGalleryActivity.this, ScanResultActivity.class);
                                intent.putExtra(IntentKey.MODEL, getCurrentCode());
                                intent.putExtra(IntentKey.IS_PICKED_FROM_GALLERY, true);
                                startActivity(intent);

                                if (!AppIronSource.getInstance().isInterstitialReady()) {
                                    AppIronSource.getInstance().loadInterstitial(PickedFromGalleryActivity.this, new AdCallback());
                                }
                            }
                        });
                    } else {
                        Intent intent = new Intent(this, ScanResultActivity.class);
                        intent.putExtra(IntentKey.MODEL, getCurrentCode());
                        intent.putExtra(IntentKey.IS_PICKED_FROM_GALLERY, true);
                        startActivity(intent);
                    }
                }
                break;

            default:
                break;
        }
    }

    private void setBack() {
        mBinding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PickedFromGalleryActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(PickedFromGalleryActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void getSetting() {
        mBinding.imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
    }
}
