package com.woslovelife.httppro;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.woslovelife.httplibs.DownloadManager;
import com.woslovelife.httplibs.HttpManager;
import com.woslovelife.httplibs.Logger;
import com.woslovelife.httplibs.NetCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QR = 0;

    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private TextView mTvState;
    private TextView mTvProgress;
    private TextView mTvMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.iv);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTvState = (TextView) findViewById(R.id.tv_state);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        mTvMax = (TextView) findViewById(R.id.tv_max);
        findViewById(R.id.btn_scan_qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 扫描二维码并在result中获取结果 */
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR);
            }
        });

        mTvState.setText("空闲中");
    }

    private void check(final String result) {
        if (result.startsWith("http://")) {
            new AlertDialog.Builder(this)
                    .setTitle(null)
                    .setMessage("点击确定开始下载")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mTvState.setText("下载状态: 下载中...");
                            mProgressBar.setProgress(0);
                            mTvProgress.setText("下载进度: 0");
                            mTvMax.setText("总大小: 0");
//                            startDownload(result);
                            multiThreadsDownload(result);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(null)
                    .setMessage("二维码内容: " + result)
                    .create()
                    .show();
        }
    }

    private void multiThreadsDownload(String url) {
        DownloadManager.getInstance().download(url, mNetCallback);
    }

    private void startDownload(String url) {
        HttpManager.getInstance().asyncReq(url, mNetCallback);
    }

    NetCallback mNetCallback = new NetCallback() {
        @Override
        public void success(final File file) {
            Logger.d("文件大小 = " + file.length());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    mImageView.setImageBitmap(bitmap);

                    Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    mTvState.setText("下载状态: 下载完成");
                }
            });
        }

        @Override
        public void fail(int code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "下载失败(" + msg + ")", Toast.LENGTH_SHORT).show();
                    mTvState.setText("下载状态: 下载失败");
                }
            });
        }

        @Override
        public void progress(final long progress, final long max) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setProgress(Math.round(progress * 100 / max));
                    mTvProgress.setText("下载进度: " + progress);
                    mTvMax.setText("总大小: " + max);
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_CODE_QR:
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        Toast.makeText(this, "解析二维码失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        check(result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        Toast.makeText(this, "解析二维码失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
