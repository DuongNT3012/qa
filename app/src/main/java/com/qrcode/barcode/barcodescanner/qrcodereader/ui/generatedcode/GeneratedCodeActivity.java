package com.qrcode.barcode.barcodescanner.qrcodereader.ui.generatedcode;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.qrcode.barcode.barcodescanner.qrcodereader.VAppUtility;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.constant.AppConstants;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.constant.IntentKey;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.model.Code;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.FileUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.PermissionUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.ProgressDialogUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.settings.SettingsActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import com.qrcode.barcode.barcodescanner.qrcodereader.R;
import com.qrcode.barcode.barcodescanner.qrcodereader.databinding.ActivityGeneratedCodeBinding;

public class GeneratedCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CODE_TO_SHARE = 1;
    private final int REQUEST_CODE_TO_SAVE = 2;
    private final int REQUEST_CODE_TO_PRINT = 3;

    private ActivityGeneratedCodeBinding mBinding;
    private Menu mToolbarMenu;
    private Code mCurrentCode;
    private Bitmap mCurrentGeneratedCodeBitmap;
    private File mCurrentCodeFile, mCurrentPrintedFile;
    private CompositeDisposable mCompositeDisposable;
    //ads
    NativeAd nativeAd;
    private FirebaseAnalytics mFirebaseAnalytics;

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        mCompositeDisposable = compositeDisposable;
    }

    public File getCurrentPrintedFile() {
        return mCurrentPrintedFile;
    }

    public void setCurrentPrintedFile(File currentPrintedFile) {
        mCurrentPrintedFile = currentPrintedFile;
    }

    public File getCurrentCodeFile() {
        return mCurrentCodeFile;
    }

    public void setCurrentCodeFile(File currentCodeFile) {
        mCurrentCodeFile = currentCodeFile;
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_generated_code);
        setCompositeDisposable(new CompositeDisposable());

        getWindow().setBackgroundDrawable(null);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //

        initializeToolbar();
        loadQRCode();
        setListeners();
        setBack();
        setBtnHome();
        getSetting();
        //add ads
        addAdsBanner();
        showNativeAd();
        //
    }

    //add ads banner
    private void addAdsBanner(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLoaded() {

                mBinding.adView.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(@NonNull AdValue adValue) {
                        VAppUtility.logAdAdmobValue(adValue,
                                mBinding.adView.getAdUnitId(),
                                mBinding.adView.getResponseInfo().getMediationAdapterClassName(),
                                mFirebaseAnalytics);
                    }
                });
            }

            @Override
            public void onAdClicked() {

            }
        });


        mBinding.adView.loadAd(adRequest);

    }
    //
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
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_native_savedQR));
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout = mBinding.flNative;
                NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_ads, null);

                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCompositeDisposable().dispose();
    }

    private void setBack(){
        mBinding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }
    private void  setBtnHome(){
        mBinding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }
    private void getSetting(){
        mBinding.imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
    }

    private void setListeners() {
        mBinding.imageViewSave.setOnClickListener(this);
        mBinding.imageViewShare.setOnClickListener(this);
        mBinding.imageViewPrint.setOnClickListener(this);
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
            ProgressDialogUtil.on().showProgressDialog(this);

            mBinding.textViewContent.setText(getCurrentCode().getContent());

            mBinding.textViewType.setText(
                    getResources().getStringArray(R.array.code_types)[getCurrentCode().getType()]);

            BarcodeFormat barcodeFormat;
            switch (getCurrentCode().getType()) {
                case Code.BAR_CODE:
                    barcodeFormat = BarcodeFormat.CODE_128;
                    break;

                case Code.QR_CODE:
                    barcodeFormat = BarcodeFormat.QR_CODE;
                    break;

                default:
                    barcodeFormat = null;
                    break;
            }

            if (barcodeFormat != null) {
                Writer multiFormatWriter = new MultiFormatWriter();
                try {
//                    int unicode = 0x1F601;
//                    String emoji = getEmojiByUnicode(unicode);
//                    String text = "So happy " + emoji;
                    String utf = convertStringToUTF8(getCurrentCode().getContent());
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                    BitMatrix bitMatrix = multiFormatWriter.encode(getCurrentCode().getContent(),
//                            barcodeFormat, 1000, 1000);
                    BitMatrix bitMatrix = multiFormatWriter.encode(utf,
                            barcodeFormat, 1000, 1000);
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//                    Toast.makeText(GeneratedCodeActivity.this,getCurrentCode().getContent(), Toast.LENGTH_LONG).show();
                    mBinding.imageViewGeneratedCode.setImageBitmap(bitmap);
                    mCurrentGeneratedCodeBitmap = bitmap;
                } catch (Exception e) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        Log.e(getClass().getSimpleName(), e.getMessage());
                    }
                }
            }

            ProgressDialogUtil.on().hideProgressDialog();
        }
    }
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
    public static String convertStringToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
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
            case R.id.image_view_print:
                if (PermissionUtil.on().requestPermission(this,
                        REQUEST_CODE_TO_PRINT, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (getCurrentPrintedFile() == null) {
                        storeCodeDocument();
                    } else {
                        Toast.makeText(this,
                                getString(R.string.generated_qr_code_already_exists),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.image_view_save:
                if (PermissionUtil.on().requestPermission(this,
                        REQUEST_CODE_TO_SAVE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (getCurrentCodeFile() == null) {
                        storeCodeImage(true);
                    } else {
                        Toast.makeText(this,
                                getString(R.string.generated_qr_code_already_exists),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.image_view_share:
                if (PermissionUtil.on().requestPermission(this,
                        REQUEST_CODE_TO_SHARE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (getCurrentCodeFile() == null) {
                        storeCodeImage(false);
                    } else {
                        shareCode(getCurrentCodeFile());
                    }
                }
                break;

            default:
                break;
        }
    }

    private void storeCodeImage(boolean justSave) {
        ProgressDialogUtil.on().showProgressDialog(this);

        getCompositeDisposable().add(
                Completable.create(emitter -> {
                    String type = getResources().getStringArray(R.array.code_types)[getCurrentCode().getType()];
                    File codeImageFile = FileUtil.getEmptyFile(this, AppConstants.PREFIX_IMAGE,
                            String.format(Locale.ENGLISH, getString(R.string.file_name_body),
                                    type.substring(0, type.indexOf(" Code")),
                                    String.valueOf(System.currentTimeMillis())),
                            AppConstants.SUFFIX_IMAGE,
                            Environment.DIRECTORY_PICTURES);

                    if (codeImageFile != null && mCurrentGeneratedCodeBitmap != null) {
                        try (FileOutputStream out = new FileOutputStream(codeImageFile)) {
                            mCurrentGeneratedCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            setCurrentCodeFile(codeImageFile);

                            if (!emitter.isDisposed()) {
                                emitter.onComplete();
                            }
                        } catch (IOException e) {
                            if (!emitter.isDisposed()) {
                                emitter.onError(e);
                            }
                        }
                    } else {
                        if (!emitter.isDisposed()) {
                            emitter.onError(new NullPointerException());
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                ProgressDialogUtil.on().hideProgressDialog();
                                if (justSave) {
                                    Toast.makeText(GeneratedCodeActivity.this,
                                            getString(R.string.saved_the_code_successfully),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    shareCode(getCurrentCodeFile());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e != null && !TextUtils.isEmpty(e.getMessage())) {
                                    Log.e(getClass().getSimpleName(), e.getMessage());
                                }

                                ProgressDialogUtil.on().hideProgressDialog();
                                if (justSave) {
                                    Toast.makeText(GeneratedCodeActivity.this,
                                            getString(R.string.failed_to_save_the_code),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(GeneratedCodeActivity.this,
                                            getString(R.string.failed_to_share_the_code), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }));
    }

    private void storeCodeDocument() {
        ProgressDialogUtil.on().showProgressDialog(this);

        getCompositeDisposable().add(
                Completable.create(emitter -> {
                    String type = getResources().getStringArray(R.array.code_types)[getCurrentCode().getType()];
                    File codeDocumentFile = FileUtil.getEmptyFile(this, AppConstants.PREFIX_CODE,
                            String.format(Locale.ENGLISH, getString(R.string.file_name_body),
                                    type.substring(0, type.indexOf(" Code")),
                                    String.valueOf(System.currentTimeMillis())),
                            AppConstants.SUFFIX_CODE,
                            Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ?
                                    Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_DOCUMENTS);

                    if (codeDocumentFile != null && mCurrentGeneratedCodeBitmap != null && getCurrentCode() != null) {
                        try {
                            Document document = new Document();

                            PdfWriter.getInstance(document, new FileOutputStream(codeDocumentFile));

                            document.open();
                            document.setPageSize(PageSize.A4);
                            document.addCreationDate();
                            document.addAuthor(getString(R.string.app_name));
                            document.addCreator(getString(R.string.app_name));

                            BaseColor colorAccent = new BaseColor(0, 153, 204, 255);
                            float headingFontSize = 20.0f;
                            float valueFontSize = 26.0f;

                            BaseFont baseFont = BaseFont.createFont("res/font/opensans_regular.ttf", "UTF-8", BaseFont.EMBEDDED);

                            LineSeparator lineSeparator = new LineSeparator();
                            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

                            // Adding Title....
                            Font mOrderDetailsTitleFont = new Font(baseFont, 36.0f, Font.NORMAL, BaseColor.BLACK);
                            Chunk mOrderDetailsTitleChunk = new Chunk("Code Details", mOrderDetailsTitleFont);
                            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
                            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
                            document.add(mOrderDetailsTitleParagraph);

                            document.add(new Paragraph(AppConstants.EMPTY_STRING));
                            document.add(Chunk.NEWLINE);
                            document.add(new Paragraph(AppConstants.EMPTY_STRING));
                            document.add(new Paragraph(AppConstants.EMPTY_STRING));
                            document.add(Chunk.NEWLINE);
                            document.add(new Paragraph(AppConstants.EMPTY_STRING));

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            mCurrentGeneratedCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            Image codeImage = Image.getInstance(stream.toByteArray());
                            codeImage.setAlignment(Image.ALIGN_CENTER);
                            codeImage.scalePercent(40);
                            Paragraph imageParagraph = new Paragraph();
                            imageParagraph.add(codeImage);
                            document.add(imageParagraph);

                            document.add(new Paragraph(AppConstants.EMPTY_STRING));
                            document.add(Chunk.NEWLINE);
                            document.add(new Paragraph(AppConstants.EMPTY_STRING));

                            // Adding Chunks for Title and value
                            Font mOrderIdFont = new Font(baseFont, headingFontSize, Font.NORMAL, colorAccent);
                            Chunk mOrderIdChunk = new Chunk("Content:", mOrderIdFont);
                            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
                            document.add(mOrderIdParagraph);

                            Font mOrderIdValueFont = new Font(baseFont, valueFontSize, Font.NORMAL, BaseColor.BLACK);
                            Chunk mOrderIdValueChunk = new Chunk(getCurrentCode().getContent(), mOrderIdValueFont);
                            Paragraph mOrderIdValueParagraph = new Paragraph(mOrderIdValueChunk);
                            document.add(mOrderIdValueParagraph);

                            document.add(new Paragraph(AppConstants.EMPTY_STRING));
                            document.add(Chunk.NEWLINE);
                            document.add(new Paragraph(AppConstants.EMPTY_STRING));

                            // Fields of Order Details...
                            Font mOrderDateFont = new Font(baseFont, headingFontSize, Font.NORMAL, colorAccent);
                            Chunk mOrderDateChunk = new Chunk("Type:", mOrderDateFont);
                            Paragraph mOrderDateParagraph = new Paragraph(mOrderDateChunk);
                            document.add(mOrderDateParagraph);

                            Font mOrderDateValueFont = new Font(baseFont, valueFontSize, Font.NORMAL, BaseColor.BLACK);
                            Chunk mOrderDateValueChunk = new Chunk(type, mOrderDateValueFont);
                            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
                            document.add(mOrderDateValueParagraph);

                            document.close();

                            setCurrentPrintedFile(codeDocumentFile);
                            if (!emitter.isDisposed()) {
                                emitter.onComplete();
                            }
                        } catch (IOException | DocumentException ie) {
                            if (!emitter.isDisposed()) {
                                emitter.onError(ie);
                            }
                        } catch (ActivityNotFoundException ae) {
                            if (!emitter.isDisposed()) {
                                emitter.onError(ae);
                            }
                        }
                    } else {
                        if (!emitter.isDisposed()) {
                            emitter.onError(new NullPointerException());
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                ProgressDialogUtil.on().hideProgressDialog();
                                Toast.makeText(GeneratedCodeActivity.this,
                                        getString(R.string.saved_the_code_successfully),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e != null && !TextUtils.isEmpty(e.getMessage())) {
                                    Log.e(getClass().getSimpleName(), e.getMessage());
                                }

                                ProgressDialogUtil.on().hideProgressDialog();
                                Toast.makeText(GeneratedCodeActivity.this,
                                        getString(R.string.failed_to_save_the_code),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void shareCode(File codeImageFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                    getString(R.string.file_provider_authority), codeImageFile));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(codeImageFile));
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_code_using)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isValid = true;

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isValid = false;
            }
        }

        switch (requestCode) {
            case REQUEST_CODE_TO_SAVE:
                if (isValid) {
                    if (getCurrentCodeFile() == null) {
                        storeCodeImage(true);
                    } else {
                        Toast.makeText(this,
                                getString(R.string.generated_qr_code_already_exists),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case REQUEST_CODE_TO_PRINT:
                if (isValid) {
                    if (getCurrentPrintedFile() == null) {
                        storeCodeDocument();
                    } else {
                        Toast.makeText(this,
                                getString(R.string.generated_qr_code_already_exists),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case REQUEST_CODE_TO_SHARE:
                if (isValid) {
                    if (getCurrentCodeFile() == null) {
                        storeCodeImage(false);

                        if (getCurrentCodeFile() != null) {
                            shareCode(getCurrentCodeFile());
                        } else {
                            Toast.makeText(this,
                                    getString(R.string.failed_to_share_the_code), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        shareCode(getCurrentCodeFile());
                    }
                }
                break;

            default:
                break;
        }
    }
}