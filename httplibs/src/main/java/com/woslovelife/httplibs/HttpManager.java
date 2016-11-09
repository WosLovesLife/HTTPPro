package com.woslovelife.httplibs;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangh on 2016/11/6.
 */

public class HttpManager {
    public static final int ERROR_CODE_NET = 0;
    public static final int ERROR_CODE_FAIL = 1;
    public static final int ERROR_CODE_WROTE_FAIL = 2;
    public static final int ERROR_CODE_UNSUPPORTED = 3;

    private static final HttpManager sManager = new HttpManager();
    private final OkHttpClient mOkHttpClient;
    private Context mContext;

    private HttpManager() {
        mOkHttpClient = new OkHttpClient();
    }

    public static HttpManager getInstance() {
        return sManager;
    }

    public void init(Context context) {
        mContext = context;
    }

    @Nullable
    public Response syncReq(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            return mOkHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Response syncReq(String url, long start, long end) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Range", "bytes=" + start + "-" + end)
                .build();
        try {
            return mOkHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void asyncReq(final String url, Callback callback) {
        final Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public void asyncReq(final String url, final NetCallback callback) {
        final long timeMillis = System.currentTimeMillis();
        asyncReq(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.fail(ERROR_CODE_NET, "网络请求异常");
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Logger.d("网络请求成功, 准备写入文件...");
                    Logger.w("网络请求成功 耗时: " + (System.currentTimeMillis() - timeMillis + "毫秒"));

                    final File file = FileManager.getInstance().getFile(url);
                    if (file == null) {
                        callback.fail(ERROR_CODE_WROTE_FAIL, "写入文件时发生错误" + response.message());
                        return;
                    }

                    InputStream inputStream = response.body().byteStream();
                    try {
                        final long max = response.body().contentLength();
                        FileManager.getInstance().write2File(inputStream, file, new FileManager.ProgressListener() {
                            @Override
                            public void progress(long progress) {
                                callback.progress(progress, max);
                            }
                        });

                        Logger.d("写入文件完成");
                        callback.success(file);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        callback.fail(ERROR_CODE_WROTE_FAIL, "写入文件时发生错误" + response.message());
                    } finally {
                        IOUtils.close(inputStream);
                    }
                    Logger.w("写入文件完成 耗时: " + (System.currentTimeMillis() - timeMillis + "毫秒"));
                } else if (response.isRedirect()) {
                    Logger.d("请求需要重定向");
                } else {
                    Logger.d("请求未能成功 " + response.message());
                    callback.fail(ERROR_CODE_FAIL, "请求未能成功 " + response.message());
                }
            }
        });
    }
}
