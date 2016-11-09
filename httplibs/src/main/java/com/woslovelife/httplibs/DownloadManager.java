package com.woslovelife.httplibs;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by YesingBeijing on 2016/11/9.
 */
public class DownloadManager {
    private static final DownloadManager sManager = new DownloadManager();
    private static final int MAX_THREAD = 2;

    private static final ThreadPoolExecutor sThreadPool = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
        /** 原子级的整数操作 */
        AtomicInteger mInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "download thread " + mInteger.getAndIncrement());
        }
    });

    private Context mContext;

    private DownloadManager() {
    }

    public static DownloadManager getInstance() {
        return sManager;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void download(final String url, final NetCallback callback) {
        HttpManager.getInstance().asyncReq(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.fail(HttpManager.ERROR_CODE_NET, "网络异常");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.fail(HttpManager.ERROR_CODE_NET, "网络异常");
                    return;
                }

                long bodyLength = response.body().contentLength();
                if (bodyLength < 0) {
                    callback.fail(HttpManager.ERROR_CODE_UNSUPPORTED, "不支持范围下载");
                    //TODO 可以采用常规方式下载
                    return;
                }

                multiThreadsDownload(url,bodyLength,callback);
            }
        });
    }

    private void multiThreadsDownload(String url, long length, NetCallback callback) {
        /* 每一条线程要处理的大小 */
        long size = length / MAX_THREAD;
        for (int i = 0; i < MAX_THREAD; i++) {
            long start = i * size;
            long end = (i + 1) * size;
            if (i == MAX_THREAD - 1) {
                end = length;
            }

            sThreadPool.execute(new DownloadRunnable(start, end, url, callback));
        }
    }
}
