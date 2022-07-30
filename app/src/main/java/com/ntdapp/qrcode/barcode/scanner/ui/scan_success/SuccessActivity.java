package com.ntdapp.qrcode.barcode.scanner.ui.scan_success;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ads.AppIronSource;
import com.example.ads.funtion.AdCallback;
import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.databinding.ActivitySuccessBinding;
import com.ntdapp.qrcode.barcode.scanner.helpers.constant.IntentKey;
import com.ntdapp.qrcode.barcode.scanner.helpers.model.Code;
import com.ntdapp.qrcode.barcode.scanner.ui.home.HomeActivity;
import com.ntdapp.qrcode.barcode.scanner.ui.scanresult.ScanResultActivity;
import com.ntdapp.qrcode.barcode.scanner.ui.tutorial.TutorialActivity;

public class SuccessActivity extends AppCompatActivity {

    ActivitySuccessBinding mBinding;
    private Code code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_success);

        if (!AppIronSource.getInstance().isInterstitialReady()) {
            AppIronSource.getInstance().loadInterstitial(this, new AdCallback());
        }

        Intent intent = getIntent();
        Bundle bundle;

        if (intent != null) {
            bundle = intent.getExtras();

            if (bundle != null && bundle.containsKey(IntentKey.MODEL)) {
                code = bundle.getParcelable(IntentKey.MODEL);
            }
        }

        mBinding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.btnViewScanResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppIronSource.getInstance().isInterstitialReady()) {
                    AppIronSource.getInstance().showInterstitial(SuccessActivity.this, new AdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Intent intent = new Intent(SuccessActivity.this, ScanResultActivity.class);
                            intent.putExtra(IntentKey.MODEL, code);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Intent intent = new Intent(SuccessActivity.this, ScanResultActivity.class);
                    intent.putExtra(IntentKey.MODEL, code);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}