package com.woslovelife.httplibs;

import java.io.File;

/**
 * Created by zhangh on 2016/11/6.
 */

public interface NetCallback {
    void success(File file);

    void fail(int code, String msg);

    void progress(long progress, long max);
}
