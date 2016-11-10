package com.woslovelife.httplibs;

import com.woslovelife.httplibs.file.FileManager;
import com.woslovelife.httplibs.utils.IOUtils;

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
        if (file == null) {
            mNetCallback.fail(HttpManager.ERROR_CODE_WROTE_FAIL, "本地文件创建失败");
            return;
        }
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(mStart);

            BufferedInputStream inputStream = new BufferedInputStream(response.body().byteStream());
            byte[] bytes = new byte[1024 * 500];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                randomAccessFile.write(bytes, 0, len);
                if (mNetCallback != null) {
                    mNetCallback.progress(len, 0);
                }
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
