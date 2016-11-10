package com.woslovelife.httplibs;

/**
 * Created by YesingBeijing on 2016/11/10.
 */
public class DownloadTask {
    private String mUrl;
    private NetCallback mNetCallback;

    public DownloadTask() {
    }

    public DownloadTask(String url, NetCallback netCallback) {
        mUrl = url;
        mNetCallback = netCallback;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public NetCallback getNetCallback() {
        return mNetCallback;
    }

    public void setNetCallback(NetCallback netCallback) {
        mNetCallback = netCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadTask that = (DownloadTask) o;

        if (mUrl != null ? !mUrl.equals(that.mUrl) : that.mUrl != null) return false;
        return mNetCallback != null ? mNetCallback.equals(that.mNetCallback) : that.mNetCallback == null;

    }

    @Override
    public int hashCode() {
        int result = mUrl != null ? mUrl.hashCode() : 0;
        result = 31 * result + (mNetCallback != null ? mNetCallback.hashCode() : 0);
        return result;
    }
}
