package com.qrcode.barcode.barcodescanner.qrcodereader.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qrcode.barcode.barcodescanner.qrcodereader.VAppUtility;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.constant.IntentKey;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.itemtouch.OnStartDragListener;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.itemtouch.SimpleItemTouchHelperCallback;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.model.Code;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.ProgressDialogUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.database.DatabaseUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.base.ItemClickListener;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.scanresult.ScanResultActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import com.qrcode.barcode.barcodescanner.qrcodereader.R;
import com.qrcode.barcode.barcodescanner.qrcodereader.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment implements OnStartDragListener, ItemClickListener<Code> {

    private Context mContext;
    private FragmentHistoryBinding mBinding;
    private CompositeDisposable mCompositeDisposable;
    private ItemTouchHelper mItemTouchHelper;
    private HistoryAdapter mAdapter;

    //ads
    private InterstitialAd mInterstitialAd;
    Code code;
    private FirebaseAnalytics mFirebaseAnalytics;
    //

    private CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    private void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        mCompositeDisposable = compositeDisposable;
    }

    public HistoryFragment() {

    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mContext != null) {
            mBinding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.recyclerViewHistory.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new HistoryAdapter(this);
            mBinding.recyclerViewHistory.setAdapter(mAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(mBinding.recyclerViewHistory);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCompositeDisposable(new CompositeDisposable());

        if (mContext == null) {
            return;
        }

        ProgressDialogUtil.on().showProgressDialog(mContext);
        getCompositeDisposable().add(DatabaseUtil.on().getAllCodes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(codeList -> {
                    if (codeList.isEmpty()) {
                        mBinding.imageViewEmptyBox.setVisibility(View.VISIBLE);
                        mBinding.textViewNoItemPlaceholder.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.textViewNoItemPlaceholder.setVisibility(View.GONE);
                        mBinding.imageViewEmptyBox.setVisibility(View.INVISIBLE);
                    }

                    getAdapter().clear();
                    getAdapter().addItem(codeList);
                    ProgressDialogUtil.on().hideProgressDialog();
                }, e -> ProgressDialogUtil.on().hideProgressDialog()));

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
        //
    }
    //

    @Override
    public void onResume() {
        super.onResume();
        createPersonalizedAd();
    }

    //

    private HistoryAdapter getAdapter() {
        return (HistoryAdapter) mBinding.recyclerViewHistory.getAdapter();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onItemClick(View view, Code item, int position) {

        code = item;
        Log.d("codeResult",code+"");
        if (mInterstitialAd != null) {

            mInterstitialAd.show(getActivity());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            Intent intent = new Intent(mContext, ScanResultActivity.class);
            intent.putExtra(IntentKey.MODEL, item);
            intent.putExtra(IntentKey.IS_HISTORY, true);
            startActivity(intent);
        }

        /*Intent intent = new Intent(mContext, ScanResultActivity.class);
        intent.putExtra(IntentKey.MODEL, item);
        intent.putExtra(IntentKey.IS_HISTORY, true);
        startActivity(intent);*/
    }

    //add ads inter splash
    private void createPersonalizedAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        createInterstitialAd(adRequest);
    }
    private void createInterstitialAd(AdRequest adRequest){
        InterstitialAd.load(mContext,getString(R.string.ad_inter_scanresult), adRequest,
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
                                Intent intent = new Intent(mContext, ScanResultActivity.class);
                                intent.putExtra(IntentKey.MODEL, code);
                                intent.putExtra(IntentKey.IS_HISTORY, true);
                                startActivity(intent);
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
}
