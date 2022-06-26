package com.ntdapp.qrcode.barcode.scanner.ui.settings;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ads.control.ads.Admod;
import com.ads.control.ads.AppOpenManager;
import com.ads.control.funtion.AdCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ntdapp.qrcode.barcode.scanner.Constant;
import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.databinding.ActivitySettingsBinding;
import com.ntdapp.qrcode.barcode.scanner.helpers.constant.PreferenceKey;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.SharedPrefUtil;
import com.ntdapp.qrcode.barcode.scanner.ui.about_us.AboutUsActivity;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ActivitySettingsBinding mBinding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean checkResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // load ads native setting
        if (Constant.REMOTE_NATIVE_SETTING) {
            try {
                Admod.getInstance().loadNativeAd(SettingsActivity.this, getString(R.string.ad_native_setting), new AdCallback() {
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        super.onUnifiedNativeAdLoaded(nativeAd);
                        NativeAdView adView = (NativeAdView) LayoutInflater.from(SettingsActivity.this).inflate(R.layout.ads_native_large, null);
                        mBinding.flNative.removeAllViews();
                        mBinding.flNative.addView(adView);
                        Admod.getInstance().populateUnifiedNativeAdView(nativeAd, adView);
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        mBinding.flNative.removeAllViews();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mBinding.flNative.removeAllViews();
            }
        } else {
            mBinding.flNative.removeAllViews();
        }

        initializeToolbar();
        loadSettings();
        setListeners();
        setBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkResume) {
            AppOpenManager.getInstance().enableAppResume();
        }
    }

    private void loadSettings() {
        mBinding.switchCompatPlaySound.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.PLAY_SOUND));
        mBinding.switchCompatVibrate.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.VIBRATE));
        mBinding.switchCompatSaveHistory.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.SAVE_HISTORY));
        mBinding.switchCompatCopyToClipboard.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.COPY_TO_CLIPBOARD));
    }

    private void setListeners() {
        mBinding.switchCompatPlaySound.setOnCheckedChangeListener(this);
        mBinding.switchCompatVibrate.setOnCheckedChangeListener(this);
        mBinding.switchCompatSaveHistory.setOnCheckedChangeListener(this);
        mBinding.switchCompatCopyToClipboard.setOnCheckedChangeListener(this);

        mBinding.textViewPlaySound.setOnClickListener(this);
        mBinding.textViewVibrate.setOnClickListener(this);
        mBinding.textViewSaveHistory.setOnClickListener(this);
        mBinding.textViewCopyToClipboard.setOnClickListener(this);
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

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_compat_play_sound:
                SharedPrefUtil.write(PreferenceKey.PLAY_SOUND, isChecked);
                break;

            case R.id.switch_compat_vibrate:
                SharedPrefUtil.write(PreferenceKey.VIBRATE, isChecked);
                break;

            case R.id.switch_compat_save_history:
                SharedPrefUtil.write(PreferenceKey.SAVE_HISTORY, isChecked);
                break;

            case R.id.switch_compat_copy_to_clipboard:
                SharedPrefUtil.write(PreferenceKey.COPY_TO_CLIPBOARD, isChecked);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_play_sound:
                mBinding.switchCompatPlaySound.setChecked(!mBinding.switchCompatPlaySound.isChecked());
                break;

            case R.id.text_view_vibrate:
                mBinding.switchCompatVibrate.setChecked(!mBinding.switchCompatVibrate.isChecked());
                break;

            case R.id.text_view_save_history:
                mBinding.switchCompatSaveHistory.setChecked(!mBinding.switchCompatSaveHistory.isChecked());
                break;

            case R.id.text_view_copy_to_clipboard:
                mBinding.switchCompatCopyToClipboard.setChecked(!mBinding.switchCompatCopyToClipboard.isChecked());
                break;

            default:
                break;
        }
    }

    public void startAboutUsActivity(View view) {

        startActivity(new Intent(this, AboutUsActivity.class));
    }

    public void startPrivacyPolicyActivity(View view) {
//        startActivity(new Intent(this, PrivayPolicyActivity.class));
        catchPrivacyPolicy();
    }

    public void startFeedbackActivity(View view) {
//        startActivity(new Intent(this, FeedbackActivity.class));
        showDialog();
    }

    private void catchPrivacyPolicy() {
        checkResume = true;
        AppOpenManager.getInstance().disableAppResume();
        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/trustqr-434a6.appspot.com/o/Privacy_policy.html?alt=media&token=95260c7c-ca4c-4b3e-a853-32a225063ba3");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.feedback_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText edt_title = (EditText) dialog.findViewById(R.id.edt_title);
        EditText edt_content = (EditText) dialog.findViewById(R.id.edt_content);
        Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
        Button btn_no = (Button) dialog.findViewById(R.id.btn_no);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkResume = true;
                AppOpenManager.getInstance().disableAppResume();
                String uriText = "mailto:" + Constant.email +
                        "?subject=" + "feedback Qr code" +
                        "&body=" + "Title : " + edt_title.getText() + "\nContent: " + edt_content.getText();
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(sendIntent, "Send Email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SettingsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
