package com.woslovelife.httplibs;

import android.content.Context;

import com.woslovelife.httplibs.db.DownloadEntity;
import com.woslovelife.httplibs.db.DownloadHelper;
import com.woslovelife.httplibs.file.FileManager;
import com.woslovelife.httplibs.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    private HashSet<DownloadTask> mTasks = new HashSet<>();

    private static final ThreadPoolExecutor sThreadPool = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
        /** 原子级的整数操作 */
        AtomicInteger mInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "download thread " + mInteger.getAndIncrement());
        }
    });

    private Context mContext;
    private List<DownloadEntity> mCache;

    private DownloadManager() {
    }

    public static DownloadManager getInstance() {
        return sManager;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void download(final String url, final NetCallback callback) {

        mCache = DownloadHelper.getInstance().getAll(url);
        if (mCache == null || mCache.size() == 0) {

        }

        final DownloadTask task = new DownloadTask(url, callback);
        if (mTasks.contains(task)) {
            /* 任务已经在队列中,不需要再次添加 */
            callback.fail(HttpManager.ERROR_CODE_TASK_EXISTED, "任务已经在队列中,不需要再次添加");
            return;
        }

        mTasks.add(task);

        HttpManager.getInstance().asyncReq(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                removeTask(task);
                if (callback != null) {
                    callback.fail(HttpManager.ERROR_CODE_NET, "网络异常");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    removeTask(task);
                    callback.fail(HttpManager.ERROR_CODE_NET, "网络异常");
                    return;
                }

                final long bodyLength = response.body().contentLength();
                if (bodyLength < 0) {
                    /* 采用常规方式下载 */
                    final File file = FileManager.getInstance().getFile(url);
                    if (file == null) {
                        removeTask(task);
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
                        removeTask(task);
                        IOUtils.close(inputStream);
                    }
                    return;
                }

                mProgress = -1;
                mSuccessThread = 0;

                multiThreadsDownload(url, bodyLength, new NetCallback() {
                    @Override
                    public void success(File file) {
                        synchronized (this) {
                            ++mSuccessThread;
                            if (mSuccessThread == MAX_THREAD) {
                                removeTask(task);
                                callback.success(file);
                            }
                        }
                    }

                    @Override
                    public void fail(int code, String msg) {
                        removeTask(task);
                        callback.fail(code, msg);
                        //TODO 结束另一条线程, 或将当前线程的进度保存起来
                    }

                    @Override
                    public void progress(long progress, long max) {
                        synchronized (this) {
                            mProgress += progress;
                            callback.progress(mProgress, bodyLength);
                        }
                    }
                });
            }
        });
    }

    private void removeTask(DownloadTask task) {
        mTasks.remove(task);
    }

    long mProgress;
    int mSuccessThread;

    private void multiThreadsDownload(String url, long length, final NetCallback callback) {
        if (mCache == null || mCache.size() == 0) {
            mCache = new ArrayList<>();
        }

        /* 每一条线程要处理的大小 */
        long size = length / MAX_THREAD;
        for (int i = 0; i < MAX_THREAD; i++) {
            long start = i * size;
            long end = (i + 1) * size;
            if (i == MAX_THREAD - 1) {
                end = length - 1;
            }

            DownloadEntity entity = new DownloadEntity();
            entity.setStart_position(start);
            entity.setEnd_position(end);
            entity.setDownload_url(url);
            entity.setThread_id((i+1));

            mCache.set((i+1),entity);

            sThreadPool.execute(new DownloadRunnable(start, end, url, callback));
        }
    }
}
