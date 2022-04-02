package com.ntdapp.qrcode.barcode.scanner.helpers.model;

import androidx.room.Dao;
import androidx.room.Query;

import com.ntdapp.qrcode.barcode.scanner.helpers.constant.TableNames;
import com.ntdapp.qrcode.barcode.scanner.helpers.util.database.BaseDao;

import java.util.List;

import io.reactivex.Flowable;

@Dao

public interface CodeDao extends BaseDao<Code> {
    @Query("SELECT * FROM " + TableNames.CODES)
    Flowable<List<Code>> getAllFlowableCodes();

}
