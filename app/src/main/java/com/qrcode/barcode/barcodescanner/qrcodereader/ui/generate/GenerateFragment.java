package com.qrcode.barcode.barcodescanner.qrcodereader.ui.generate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qrcode.barcode.barcodescanner.qrcodereader.VAppUtility;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.constant.IntentKey;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.model.Code;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.generatedcode.GeneratedCodeActivity;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qrcode.barcode.barcodescanner.qrcodereader.R;
import com.qrcode.barcode.barcodescanner.qrcodereader.databinding.FragmentGenerateBinding;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.home.HomeActivity;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.splash.SplashActivity;

public class GenerateFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private FragmentGenerateBinding mBinding;
    private Context mContext;
//    private InterstitialAd mInterstitialAd;
    private String contentRevert;

    //ads
    NativeAd nativeAd;
    private InterstitialAd mInterstitialAd;
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
//        initializeAd();

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        //
        //ads
        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                createPersonalizedAd();
            }
        });

        //ads native
        showNativeAd();
        //

        setListeners();
        initializeCodeTypesSpinner();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //
        createPersonalizedAd();
        //
    }

    private void initializeAd() {
        if (mContext == null) {
            return;
        }

//        mInterstitialAd = new InterstitialAd(mContext);
//        mInterstitialAd.setAdUnitId(getString(R.string.admob_test_interstitial_ad_unit_id));
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

//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when the ad is displayed.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when when the interstitial ad is closed.
//                generateCode();
//            }
//        });
    }

    private void initializeCodeTypesSpinner() {
//        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(mContext,
//                R.array.code_types, android.R.layout.simple_spinner_item);
//        arrayAdapter.setDropDownViewResource(R.layout.item_spinner);
//        mBinding.spinnerTypes.setAdapter(arrayAdapter);
        String[] code_types = {"Select Type","QR Code","Bar Code"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_text,  code_types );
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
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                } else {
//                    generateCode();
//                }
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
                contentRevert= content;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            int type = mBinding.spinnerTypes.getSelectedItemPosition();
            typeQR=type;

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
                    if (type == 2 && getSpecialCharacterCount(content) == false){
                        Toast.makeText(mContext,
                                getString(R.string.invalid_entry),
                                Toast.LENGTH_SHORT).show();
                    }else {
                        //startActivity(intent);
                        //show ads
                        if (mInterstitialAd != null) {


                            mInterstitialAd.show(getActivity());
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                            startActivity(intent);
                        }
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

    //add ads inter
    private void createPersonalizedAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        createInterstitialAd(adRequest);
    }
    private void createInterstitialAd(AdRequest adRequest){
        InterstitialAd.load(mContext,getString(R.string.ad_inter_generate), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        //log event
                        mInterstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                VAppUtility.logAdAdmobValue(adValue,
                                        mInterstitialAd.getAdUnitId(),
                                        mInterstitialAd.getResponseInfo().getMediationAdapterClassName(),
                                        mFirebaseAnalytics);
                            }
                        });
                        //

                        Log.i("TAG", "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                //generateCode();
                                startGenerated(contentRevert,typeQR);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
    //
    private void startGenerated(String context,int type){
        Intent intent = new Intent(mContext, GeneratedCodeActivity.class);
        Code code = new Code(context, type);
        intent.putExtra(IntentKey.MODEL, code);
        startActivity(intent);
    }
    //ads native
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
    private void showNativeAd() {
        AdLoader.Builder builder = new AdLoader.Builder(mContext, getString(R.string.ad_native_createQR));
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                if (isAdded()) {
                    nativeAd = unifiedNativeAd;
                    FrameLayout frameLayout = mBinding.flNative;
                    NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_ads, null);

                    populateUnifiedNativeAdView(unifiedNativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);

                }

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
    //end
}
