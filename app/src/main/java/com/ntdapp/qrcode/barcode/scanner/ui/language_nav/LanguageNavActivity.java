package com.ntdapp.qrcode.barcode.scanner.ui.language_nav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.ui.SystemUtil;
import com.ntdapp.qrcode.barcode.scanner.ui.home.HomeActivity;
import com.ntdapp.qrcode.barcode.scanner.ui.language.LanguageModel;
import com.ntdapp.qrcode.barcode.scanner.ui.language_nav.adapter.LanguageAdapterNav;

import java.util.ArrayList;
import java.util.List;

public class LanguageNavActivity extends AppCompatActivity implements IClickLanguageNav {
    private LanguageAdapterNav adapter = null;
    private LanguageModelNav modelNav = new LanguageModelNav();
    private SharedPreferences sharedPreferences = null;
    private RecyclerView rcl_language;
    private ImageView iv_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_nav);

        rcl_language = findViewById(R.id.rcl_language);
        iv_back = findViewById(R.id.iv_back);
        // load and show ads native language
        sharedPreferences = getSharedPreferences("MY_PRE", MODE_PRIVATE);

        adapter = new LanguageAdapterNav(this, setLanguageDefault(), this);
        rcl_language.setAdapter(adapter);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSubOrTur();
                finish();
            }
        });
    }

    @Override
    public void onClick(LanguageModelNav data) {
        adapter.setSelectLanguage(data);
        modelNav = data;

        if (modelNav != null) {
            SystemUtil.setPreLanguage(LanguageNavActivity.this, modelNav.isoLanguage);
        }
        /*val editor = getSharedPreferences("MY_PRE", MODE_PRIVATE).edit()
        editor.putBoolean("nativeLanguage", true)
        editor.apply()*/
        SystemUtil.setLocale(this);
        startSubOrTur();
        finish();
    }

    private List<LanguageModelNav> setLanguageDefault() {
        ArrayList<LanguageModelNav> lists = new ArrayList();
        String key = SystemUtil.getPreLanguage(this);
        /*lists.add(new LanguageModelNav("English", "en", false));
        lists.add(new LanguageModelNav("Korean", "ko", false));
        lists.add(new LanguageModelNav("Japanese", "ja", false));
        lists.add(new LanguageModelNav("French", "fr", false));
        lists.add(new LanguageModelNav("Hindi", "hi", false));
        lists.add(new LanguageModelNav("Portuguese", "pt", false));
        lists.add(new LanguageModelNav("Spanish", "es", false));
        lists.add(new LanguageModelNav("Indonesian", "in", false));
        lists.add(new LanguageModelNav("Malay", "ms", false));
        lists.add(new LanguageModelNav("Philippines", "phi", false));
        lists.add(new LanguageModelNav("Chinese", "zh", false));
        lists.add(new LanguageModelNav("German", "de", false));*/
        lists.add(new LanguageModelNav("English", "en", false, R.drawable.ic_english_flag));
        lists.add(new LanguageModelNav("French", "fr", false, R.drawable.ic_french_flag));
        lists.add(new LanguageModelNav("Portuguese", "pt", false, R.drawable.ic_portuguese_flag));
        lists.add(new LanguageModelNav("Spanish", "es", false, R.drawable.ic_spanish));
        lists.add(new LanguageModelNav("German", "de", false, R.drawable.ic_german_flag));
        for (int i = 0; i < lists.size(); i++) {
            if (!sharedPreferences.getBoolean("nativeLanguage", false)) {
                if (key.equals(lists.get(i).isoLanguage)) {
                    LanguageModelNav data = lists.get(i);
                    data.isCheck = true;
                    lists.remove(lists.get(i));
                    lists.add(0, data);
                    break;
                }
            } else {
                if (key == lists.get(i).isoLanguage) {
                    lists.get(i).isCheck = true;
                }
            }
        }
        return lists;
    }

    private void startSubOrTur() {
        showActivity(HomeActivity.class);
    }

    private void showActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
