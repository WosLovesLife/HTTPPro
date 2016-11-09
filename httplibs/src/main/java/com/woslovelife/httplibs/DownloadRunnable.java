package com.woslovelife.httplibs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * Created by YesingBeijing on 2016/11/9.
 */
public class DownloadRunnable implements Runnable {

    private long mStart;
    private long mEnd;
    private String mUrl;
    private NetCallback mNetCallback;

    public DownloadRunnable(long start, long end, String url, NetCallback netCallback) {
        mStart = start;
        mEnd = end;
        mUrl = url;
        mNetCallback = netCallback;
    }

    @Override
    public void run() {
        Response response = HttpManager.getInstance().syncReq(mUrl, mStart, mEnd);
        if (response == null && mNetCallback != null) {
            mNetCallback.fail(HttpManager.ERROR_CODE_NET, "网络异常");
            return;
        }

        File file = FileManager.getInstance().getFile(mUrl);
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(mStart);

            BufferedInputStream inputStream = new BufferedInputStream(response.body().byteStream());
            byte[] bytes = new byte[1024 * 500];
            int len;
            int count = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                randomAccessFile.write(bytes, 0, len);
                count += len;

            }
            if (mNetCallback != null) {
                mNetCallback.success(file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(randomAccessFile);
        }
    }
}
