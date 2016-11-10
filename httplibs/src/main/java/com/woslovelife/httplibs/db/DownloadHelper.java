package com.woslovelife.httplibs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by YesingBeijing on 2016/11/10.
 */
public class DownloadHelper {

    private static final DownloadHelper sDownloadHelper = new DownloadHelper();

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private DownloadEntityDao mEntityDao;

    private DownloadHelper() {

    }

    public static DownloadHelper getInstance() {
        return sDownloadHelper;
    }

    public void init(Context context) {
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(context, "download.db", null).getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        mEntityDao = mDaoSession.getDownloadEntityDao();
    }

    public void insert(DownloadEntity entity) {
        mEntityDao.insertOrReplace(entity);
    }

    public List<DownloadEntity> getAll(String url) {
        return mEntityDao.queryBuilder()
                .where(DownloadEntityDao.Properties.Download_url.eq(url))
                .orderAsc(DownloadEntityDao.Properties.Thread_id)
                .list();
    }
}