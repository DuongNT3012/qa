package com.ntdapp.qrcode.barcode.scanner.ui.generate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ntdapp.qrcode.barcode.scanner.helpers.constant.IntentKey;
import com.ntdapp.qrcode.barcode.scanner.helpers.model.Code;
import com.ntdapp.qrcode.barcode.scanner.ui.generatedcode.GeneratedCodeActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.databinding.FragmentGenerateBinding;

public class GenerateFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private FragmentGenerateBinding mBinding;
    private Context mContext;
    private String contentRevert;
    private int typeQR;
    private FirebaseAnalytics mFirebaseAnalytics;


    public GenerateFragment() {

    }

    public static GenerateFragment newInstance() {
        return new GenerateFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_generate, container, false);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        //

        setListeners();
        initializeCodeTypesSpinner();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setListeners() {
        mBinding.spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getSelectedView()).setTextColor(ContextCompat.getColor(mContext,
                        position == 0 ? R.color.text_hint : R.color.text_regular));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.textViewGenerate.setOnClickListener(this);
    }

    private void initializeCodeTypesSpinner() {
//        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(mContext,
//                R.array.code_types, android.R.layout.simple_spinner_item);
//        arrayAdapter.setDropDownViewResource(R.layout.item_spinner);
//        mBinding.spinnerTypes.setAdapter(arrayAdapter);
        String[] code_types = {"Select Type", "QR Code", "Bar Code"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_text, code_types);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        mBinding.spinnerTypes.setAdapter(langAdapter);
    }

    @Override
    public void onClick(View view) {
        if (mContext == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.text_view_generate:
                generateCode();
                break;

            default:
                break;
        }
    }

    private void generateCode() {
        Intent intent = new Intent(mContext, GeneratedCodeActivity.class);
        if (mBinding.editTextContent.getText() != null) {
            String content = mBinding.editTextContent.getText().toString().trim();
            try {
                contentRevert = content;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            int type = mBinding.spinnerTypes.getSelectedItemPosition();
            typeQR = type;

            if (!TextUtils.isEmpty(contentRevert) && type != 0) {

                boolean isValid = true;

                switch (type) {
                    case Code.BAR_CODE:
                        if (contentRevert.length() > 80) {
                            Toast.makeText(mContext,
                                    getString(R.string.error_barcode_content_limit),
                                    Toast.LENGTH_SHORT).show();
                            isValid = false;
                        }
                        break;

                    case Code.QR_CODE:
                        if (contentRevert.length() > 1000) {
                            Toast.makeText(mContext,
                                    getString(R.string.error_qrcode_content_limit),
                                    Toast.LENGTH_SHORT).show();
                            isValid = false;
                        }
                        break;

                    default:
                        isValid = false;
                        break;
                }

                if (isValid) {
                    Code code = new Code(contentRevert, type);
                    intent.putExtra(IntentKey.MODEL, code);
                    if (type == 2 && getSpecialCharacterCount(content) == false) {
                        Toast.makeText(mContext,
                                getString(R.string.invalid_entry),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(mContext,
                        getString(R.string.error_provide_proper_content_and_type),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean getSpecialCharacterCount(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(s);
        // boolean b = m.matches();
        boolean b = ((Matcher) m).find();
        if (b == true)
            return false;
        else
            return true;
    }

    public static String stringToUnicode(String strText) throws Exception {
        char c;/*from w ww  . j  a  va  2  s .  c  o  m*/
        String strRet = "";
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128) {
                strRet += "\\u" + strHex;
            } else {
                strRet += "\\u00" + strHex;
            }
        }
        return strRet;
    }

    //
    private void startGenerated(String context, int type) {
        Intent intent = new Intent(mContext, GeneratedCodeActivity.class);
        Code code = new Code(context, type);
        intent.putExtra(IntentKey.MODEL, code);
        startActivity(intent);
    }
    //end
}
