package com.woslovelife.httplibs;

import android.util.Log;

/**
 * Created by zhangh on 2016/11/6.
 */

public class Logger {

    public static final String TAG = "WosLovesLife";
    public static final boolean DEBUG = true;

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String msg, Throwable e) {
        if (DEBUG) {
            Log.e(TAG, msg, e);
        }
    }
}
