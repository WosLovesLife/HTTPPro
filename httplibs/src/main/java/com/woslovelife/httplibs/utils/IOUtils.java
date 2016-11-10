package com.woslovelife.httplibs.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by zhangh on 2016/11/6.
 */

public class IOUtils {
    public static void close(Closeable closeable){
        if (closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
