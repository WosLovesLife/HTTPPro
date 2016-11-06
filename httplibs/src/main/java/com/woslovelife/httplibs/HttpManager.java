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

    private static final HttpManager sManager = new HttpManager();
    private Context mContext;

    private HttpManager() {
    }

    public static HttpManager getInstance() {
        return sManager;
    }

    public void init(Context context) {
        mContext = context;
    }

    @Nullable
    public Response syncReq(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void asyncReq(final String url, final NetCallback callback) {
        final long timeMillis = System.currentTimeMillis();
        OkHttpClient okHttpClient = new OkHttpClient();

        final Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                callback.fail(0, "网络请求异常");
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Logger.d("网络请求成功, 准备写入文件...");
                    Logger.w("网络请求成功 耗时: " + (System.currentTimeMillis() - timeMillis + "毫秒"));

                    final File file = FileManager.getInstance().getFile(url);
                    if (file == null) {
                        callback.fail(2, "写入文件时发生错误" + response.message());
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
                        callback.fail(2, "写入文件时发生错误" + response.message());
                    }finally {
                        IOUtils.close(inputStream);
                    }
                    Logger.w("写入文件完成 耗时: " + (System.currentTimeMillis() - timeMillis + "毫秒"));
                } else if (response.isRedirect()) {
                    Logger.d("请求需要重定向");
                } else {
                    Logger.d("请求未能成功 " + response.message());
                    callback.fail(1, "请求未能成功 " + response.message());
                }
            }
        });
    }
}
