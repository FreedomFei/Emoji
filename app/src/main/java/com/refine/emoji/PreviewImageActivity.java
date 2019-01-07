package com.refine.emoji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.bumptech.glide.request.target.Target;
import com.refine.emoji.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewImageActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivPreviewImage;
    private TextView tvName;

    private Intent mIntent;

    /**
     * 预览、下载
     */
    private String mId;
    private String mName;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        initView();
        initData();
    }

    private void initView() {
        ivPreviewImage = findViewById(R.id.iv_preview_image);
        tvName = findViewById(R.id.tv_name);
        findViewById(R.id.btn_download).setOnClickListener(this);
    }

    private void initData() {
        mIntent = getIntent();
        if (mIntent != null) {
            //预览
            mId = mIntent.getStringExtra("id");
            mName = mIntent.getStringExtra("name");
            mUrl = mIntent.getStringExtra("url");

            tvName.setText(String.valueOf(mName));
            GlideApp.with(this).load(mUrl).override(Target.SIZE_ORIGINAL).into(ivPreviewImage);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
                downloadFile();
                break;
            default:
                break;
        }
    }

    /**
     * 下载文件
     */
    private void downloadFile() {
        final String name = tvName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            toast("null!");
            return;
        }

        AVFile avFile = new AVFile();
        avFile.setObjectId(mId);
        avFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                File downloadCacheDirectory = Environment.getDownloadCacheDirectory();

                //String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test";

                // bytes 就是文件的数据流
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(
                            downloadCacheDirectory + File.separator + name);
                    outputStream.write(bytes);
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                // 下载进度数据，integer 介于 0 和 100。
                if (integer == 100) {
                    toast("下载完成！");
                }
            }
        });
    }

}
