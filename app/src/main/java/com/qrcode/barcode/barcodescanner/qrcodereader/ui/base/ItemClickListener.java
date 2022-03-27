package com.qrcode.barcode.barcodescanner.qrcodereader.ui.base;

import android.view.View;

public interface ItemClickListener<T> {
    void onItemClick(View view, T item, int position);
}
