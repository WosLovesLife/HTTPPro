package com.woslovelife.httppro;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.woslovelife.httplibs.DownloadManager;
import com.woslovelife.httplibs.db.DownloadHelper;
import com.woslovelife.httplibs.file.FileManager;
import com.woslovelife.httplibs.HttpManager;

/**
 * Created by zhangh on 2016/11/6.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        HttpManager.getInstance().init(this);
        FileManager.getInstance().init(this);
        DownloadManager.getInstance().init(this);
        DownloadHelper.getInstance().init(this);

        ZXingLibrary.initDisplayOpinion(this);
    }
}
