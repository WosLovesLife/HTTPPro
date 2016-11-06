package com.woslovelife.httppro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.woslovelife.httplibs.HttpManager;
import com.woslovelife.httplibs.Logger;
import com.woslovelife.httplibs.NetCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity {

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

        mTvState.setText("下载状态: 下载中...");
        HttpManager.getInstance().asyncReq("http://dl2.smartisan.cn/app/handshaker/win/simple/HandShakerSimpleSetup_Win8_Win10.exe", new NetCallback() {
            @Override
            public void success(File file) {
//                final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Logger.d("文件大小 = " + file.length());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mImageView.setImageBitmap(bitmap);
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
        });
    }
}
