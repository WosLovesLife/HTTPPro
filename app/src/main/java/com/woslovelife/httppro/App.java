package com.woslovelife.httppro;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.woslovelife.httplibs.FileManager;
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

        ZXingLibrary.initDisplayOpinion(this);
    }
}
