package com.woslovelife.httplibs;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    private static final ThreadPoolExecutor sThreadPool = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
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
                    /* 采用常规方式下载 */
                    final File file = FileManager.getInstance().getFile(url);
                    if (file == null) {
                        callback.fail(HttpManager.ERROR_CODE_WROTE_FAIL, "写入文件时发生错误" + response.message());
                        return;
                    }

                    InputStream inputStream = response.body().byteStream();
                    try {
                        FileManager.getInstance().write2File(inputStream, file, null);
                        callback.success(file);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        callback.fail(HttpManager.ERROR_CODE_WROTE_FAIL, "写入文件时发生错误" + response.message());
                    } finally {
                        IOUtils.close(inputStream);
                    }
                    return;
                }

                mProgress = -1;
                mSuccessThread = 0;
                multiThreadsDownload(url, bodyLength, callback);
            }
        });
    }

    long mProgress;
    int mSuccessThread;

    private void multiThreadsDownload(String url, long length, final NetCallback callback) {
        final long contentLength = length;

        /* 每一条线程要处理的大小 */
        long size = length / MAX_THREAD;
        for (int i = 0; i < MAX_THREAD; i++) {
            long start = i * size;
            long end = (i + 1) * size;
            if (i == MAX_THREAD - 1) {
                end = length;
            }

            sThreadPool.execute(new DownloadRunnable(start, end, url, new NetCallback() {
                @Override
                public void success(File file) {
                    synchronized (this) {
                        ++mSuccessThread;
                        if (mSuccessThread == MAX_THREAD) {
                            callback.success(file);
                        }
                    }
                }

                @Override
                public void fail(int code, String msg) {
                    callback.fail(code, msg);
                    //TODO 结束另一条线程, 或将当前线程的进度保存起来
                }

                @Override
                public void progress(long progress, long max) {
                    synchronized (this) {
                        mProgress += progress;
                        callback.progress(mProgress, contentLength);
                    }
                }
            }));
        }
    }
}
