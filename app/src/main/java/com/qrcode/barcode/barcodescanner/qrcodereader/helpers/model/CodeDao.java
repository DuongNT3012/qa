package com.qrcode.barcode.barcodescanner.qrcodereader.helpers.model;

import androidx.room.Dao;
import androidx.room.Query;

import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.constant.TableNames;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.database.BaseDao;

import java.util.List;

import io.reactivex.Flowable;

@Dao

public interface CodeDao extends BaseDao<Code> {
    @Query("SELECT * FROM " + TableNames.CODES)
    Flowable<List<Code>> getAllFlowableCodes();

}
