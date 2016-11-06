package com.woslovelife.httplibs;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhangh on 2016/11/6.
 */

public class FileManager {
    private static final FileManager sFileManager = new FileManager();
    private Context mContext;

    private FileManager() {
    }

    public static FileManager getInstance() {
        return sFileManager;
    }

    public void init(Context context) {
        mContext = context;
    }

    @Nullable
    public File getFile(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        File dir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = mContext.getExternalCacheDir();
        } else {
            dir = mContext.getCacheDir();
        }

        String md5Url = MD5Utils.encoding(url);

        File file = new File(dir, md5Url);
        if (!file.exists()) {
            try {
                boolean isNew = file.createNewFile();
                if (!isNew) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public void write2File(InputStream in, File file, ProgressListener listener) throws IOException {
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            long progress = 0;
            inputStream = new BufferedInputStream(in);
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            byte[] bytes = new byte[1024 * 500];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
                progress += len;
                if (listener != null) {
                    listener.progress(progress);
                }
            }
            Logger.d("写入文件完成");
        } finally {
            IOUtils.close(outputStream);
        }
    }

    public interface ProgressListener {
        void progress(long progress);
    }
}
