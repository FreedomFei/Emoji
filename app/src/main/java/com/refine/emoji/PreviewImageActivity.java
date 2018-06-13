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
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.request.target.Target;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.refine.emoji.base.BaseActivity;
import com.refine.emoji.util.LogUtils;
import com.refine.emoji.util.ThreadFactoryUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PreviewImageActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivPreviewImage;
    private EditText etName;

    private Intent intent;
    private UploadPregressDialog mUploadPregressDialog;

    /**
     * 修改、预览
     */
    private String id;
    private String name;
    private String url;


    /**
     * 添加
     * 真实路径
     */
    private String realPath;

    /**
     * 添加
     * 后缀名
     */
    private String nameSuffix;

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
        intent = getIntent();
        if (intent.getData() == null) {
            //预览
            id = intent.getStringExtra("id");
            name = intent.getStringExtra("name");
            url = intent.getStringExtra("url");

            etName.setText(String.valueOf(name));
            GlideApp.with(this).load(url).override(Target.SIZE_ORIGINAL).into(ivPreviewImage);

            try {
                AVFile avFile = AVFile.withObjectId(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //添加
            realPath = RealPathUtil.getRealPath(this, intent.getData());
            int index = realPath.lastIndexOf(".");
            name = realPath.substring(0, index);
            nameSuffix = realPath.substring(index, realPath.length());

            //Bitmap bitmap = data.getExtras().getParcelable("data");
            //MediaType.parse(getContentResolver().getType(tempUri))
            GlideApp.with(this).load(intent.getData()).override(Target.SIZE_ORIGINAL).into(ivPreviewImage);

            assetsToStorage(dirPath + File.separator + language, language);
        }
    }

    String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tessdata";
    String language = "chi_sim.traineddata";

    public void assetsToStorage(String path, String name) {
        //boolean mkdirs = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tessdata")
        //        .mkdirs();

        File file1 = new File(path);
        if (!file1.exists()) {
            File p = file1.getParentFile();
            if (!p.isDirectory()) {
                p.mkdirs();
            }
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getAssets().open(language);
            File file = new File(path);
            outputStream = new FileOutputStream(file);
            byte[] bytes = new byte[2048];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_automatic_identification:
                automaticIdentification();
                break;
            case R.id.btn_upload:
                uploadFile();
                break;
            default:
                break;
        }
    }

    private void automaticIdentification() {
        ThreadFactoryUtils factoryUtils = new ThreadFactoryUtils(1);
        factoryUtils.newThread(new Runnable() {
            @Override
            public void run() {
                TessBaseAPI tessBaseAPI = new TessBaseAPI();
                tessBaseAPI.init(Environment.getExternalStorageDirectory().getAbsolutePath(), "chi_sim");
                tessBaseAPI.setImage(BitmapFactory.decodeFile(realPath));
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

    private void uploadFile() {
        String name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            toast("null!");
            return;
        }
        try {
            mUploadPregressDialog = new UploadPregressDialog(this);
            mUploadPregressDialog.show();
            AVFile avFile = AVFile.withAbsoluteLocalPath(name + nameSuffix
                    , realPath);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mUploadPregressDialog != null && mUploadPregressDialog.isShowing()) {
            mUploadPregressDialog.dismiss();
        }
        super.onDestroy();
    }
}
