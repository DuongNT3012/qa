package com.ntdapp.qrcode.barcode.scanner.ui.about_us;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ntdapp.qrcode.barcode.scanner.BuildConfig;
import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.databinding.ActivityAboutUsBinding;
import com.ntdapp.qrcode.barcode.scanner.ui.SystemUtil;
import com.ntdapp.qrcode.barcode.scanner.ui.language.LanguageActivity;

public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding mActivityAboutUsBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        mActivityAboutUsBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        initializeToolbar();
        mActivityAboutUsBinding.textViewAboutVersion.setText(getResources().getString(R.string.Version) + ": " + BuildConfig.VERSION_NAME);
        setBack();
    }

    private void initializeToolbar() {
        setSupportActionBar(mActivityAboutUsBinding.toolbar);

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
        mActivityAboutUsBinding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
