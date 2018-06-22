package com.refine.emoji;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetFileCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.request.target.Target;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.refine.emoji.base.BaseActivity;
import com.refine.emoji.util.AssetsToStorageUtils;
import com.refine.emoji.util.LogUtils;
import com.refine.emoji.util.ThreadFactoryUtils;

import java.io.File;

public class PreviewImageActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivPreviewImage;
    private EditText etName;

    private Intent mIntent;
    private UploadPregressDialog mUploadPregressDialog;

    /**
     * 修改、预览
     */
    private String mId;
    private String mName;
    private String mUrl;


    /**
     * 添加
     * 真实路径
     */
    private String mRealPath;

    /**
     * 添加
     * 后缀名
     */
    private String mNameSuffix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        initView();
        initData();
    }

    private void initView() {
        ivPreviewImage = findViewById(R.id.iv_preview_image);
        etName = findViewById(R.id.et_name);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_automatic_identification).setOnClickListener(this);
    }

    private void initData() {
        mIntent = getIntent();
        if (mIntent.getData() == null) {
            //预览
            mId = mIntent.getStringExtra("id");
            mName = mIntent.getStringExtra("name");
            mUrl = mIntent.getStringExtra("url");

            etName.setText(String.valueOf(mName));
            etName.setSelection(mName.length());
            GlideApp.with(this).load(mUrl).override(Target.SIZE_ORIGINAL).into(ivPreviewImage);
        } else {
            //添加
            mRealPath = RealPathUtil.getRealPath(this, mIntent.getData());
            int index = mRealPath.lastIndexOf(".");
            mName = mRealPath.substring(0, index);
            mNameSuffix = mRealPath.substring(index, mRealPath.length());

            //Bitmap bitmap = data.getExtras().getParcelable("data");
            //MediaType.parse(getContentResolver().getType(tempUri))
            GlideApp.with(this).load(mIntent.getData()).override(Target.SIZE_ORIGINAL).into(ivPreviewImage);

            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tessdata";
            new AssetsToStorageUtils(this).assetsToStorage(dirPath, AssetsToStorageUtils.LANGUAGE_CHI_SIM);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_automatic_identification:
                automaticIdentification();
                break;
            case R.id.btn_upload:
                mUploadPregressDialog = new UploadPregressDialog(this);
                mUploadPregressDialog.show();

                if (mIntent.getData() == null) {
                    updateFileName();
                } else {
                    uploadNewFile();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 自动识别
     */
    private void automaticIdentification() {
        ThreadFactoryUtils factoryUtils = new ThreadFactoryUtils(1);
        factoryUtils.newThread(new Runnable() {
            @Override
            public void run() {
                TessBaseAPI tessBaseAPI = new TessBaseAPI();
                tessBaseAPI.init(Environment.getExternalStorageDirectory().getAbsolutePath(), "chi_sim");
                tessBaseAPI.setImage(BitmapFactory.decodeFile(mRealPath));
                final String text = tessBaseAPI.getUTF8Text();
                tessBaseAPI.end();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        etName.setText(text);
                        etName.setSelection(text.length());
                    }
                });
            }
        }).start();
    }

    /**
     * 上传新文件
     */
    private void uploadNewFile() {
        String name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            toast("null!");
            return;
        }
        try {
            AVFile avFile = AVFile.withAbsoluteLocalPath(name + mNameSuffix
                    , mRealPath);
            uploadInBackground(avFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改文件名
     */
    private void updateFileName() {
        final String name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            toast("null!");
            return;
        }
        try {
            AVFile.withObjectIdInBackground(mId, new GetFileCallback<AVFile>() {
                @Override
                public void done(AVFile avFile, AVException e) {
                    avFile.getMetaData().put("_name", name);
                    uploadInBackground(avFile);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadInBackground(AVFile avFile) {
        avFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                // 成功或失败处理
                toast("success!");
                finish();
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                LogUtils.e(String.valueOf(integer));
                // 上传进度数据，integer 介于 0 和 100。
                mUploadPregressDialog.setProgress(integer);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mUploadPregressDialog != null && mUploadPregressDialog.isShowing()) {
            mUploadPregressDialog.dismiss();
        }
        super.onDestroy();
    }
}
