package com.ntdapp.qrcode.barcode.scanner.ui.history;

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

import com.ads.control.ads.Admod;
import com.ads.control.funtion.AdCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ntdapp.qrcode.barcode.scanner.Constant;
import com.ntdapp.qrcode.barcode.scanner.helpers.constant.IntentKey;
import com.ntdapp.qrcode.barcode.scanner.helpers.itemtouch.OnStartDragListener;
import com.ntdapp.qrcode.barcode.scanner.helpers.itemtouch.SimpleItemTouchHelperCallback;
import com.ntdapp.qrcode.barcode.scanner.helpers.model.Code;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.ProgressDialogUtil;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.database.DatabaseUtil;
import com.ntdapp.qrcode.barcode.scanner.ui.base.ItemClickListener;
import com.ntdapp.qrcode.barcode.scanner.ui.scanresult.ScanResultActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import com.ntdapp.qrcode.barcode.scanner.R;
import com.ntdapp.qrcode.barcode.scanner.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment implements OnStartDragListener, ItemClickListener<Code> {

    private Context mContext;
    private FragmentHistoryBinding mBinding;
    private CompositeDisposable mCompositeDisposable;
    private ItemTouchHelper mItemTouchHelper;
    private HistoryAdapter mAdapter;
    Code code;
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialScanResult;
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
        if (Constant.REMOTE_INTER_SCAN_RESULT) {
            if (mInterstitialScanResult == null) {
                loadInterScanResult();
            }
        }
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
    }
    //

    @Override
    public void onResume() {
        super.onResume();
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
        Admod.getInstance().forceShowInterstitial(mContext, mInterstitialScanResult, new AdCallback() {
            @Override
            public void onAdClosed() {
                code = item;
                Log.d("codeResult", code + "");

                Intent intent = new Intent(mContext, ScanResultActivity.class);
                intent.putExtra(IntentKey.MODEL, item);
                intent.putExtra(IntentKey.IS_HISTORY, true);
                startActivity(intent);
                mInterstitialScanResult = null;
                loadInterScanResult();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) {
                onAdClosed();
            }
        });
        /*code = item;
        Log.d("codeResult", code + "");

        Intent intent = new Intent(mContext, ScanResultActivity.class);
        intent.putExtra(IntentKey.MODEL, item);
        intent.putExtra(IntentKey.IS_HISTORY, true);
        startActivity(intent);*/
    }

    private void loadInterScanResult() {
        Admod.getInstance().getInterstitalAds(mContext, getString(R.string.inter_scan_result), new AdCallback(){
            @Override
            public void onInterstitialLoad(@Nullable InterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                mInterstitialScanResult = interstitialAd;
            }
            @Override
            public void onAdFailedToLoad(@Nullable LoadAdError i) {
                super.onAdFailedToLoad(i);
            }
        });
    }
}
