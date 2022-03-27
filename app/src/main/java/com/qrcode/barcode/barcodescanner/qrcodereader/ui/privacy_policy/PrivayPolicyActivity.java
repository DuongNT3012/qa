package com.qrcode.barcode.barcodescanner.qrcodereader.ui.privacy_policy;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.qrcode.barcode.barcodescanner.qrcodereader.R;
import com.qrcode.barcode.barcodescanner.qrcodereader.databinding.ActivityPrivayPolicyBinding;

public class PrivayPolicyActivity extends AppCompatActivity {

    ActivityPrivayPolicyBinding activity_privay_policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_privay_policy= DataBindingUtil.setContentView(this, R.layout.activity_privay_policy);
        initializeToolbar();
        setBack();
    }


    private void initializeToolbar() {
        setSupportActionBar(activity_privay_policy.toolbar);

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
    private void setBack(){
        activity_privay_policy.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
