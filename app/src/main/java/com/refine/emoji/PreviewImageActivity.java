package com.refine.emoji;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.request.target.Target;
import com.refine.emoji.base.BaseActivity;
import com.refine.emoji.util.LogUtils;

import java.io.FileNotFoundException;

public class PreviewImageActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivPreviewImage;
    private EditText etName;
    private Button btnUpload;

    private Intent intent;
    private UploadPregressDialog mUploadPregressDialog;

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
        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);
    }

    String id;
    String name;
    String url;

    private void initData() {
        intent = getIntent();
        if (intent.getData() == null) {
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
            GlideApp.with(this).load(intent.getData()).override(Target.SIZE_ORIGINAL).into(ivPreviewImage);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                String name = etName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    toast("null!");
                    return;
                }
                try {
                    String realPath = RealPathUtil.getRealPath(this, intent.getData());
                    String nameSuffix = realPath.substring(realPath.lastIndexOf("."), realPath.length());

                    mUploadPregressDialog = new UploadPregressDialog(this);
                    mUploadPregressDialog.show();
                    AVFile avFile = AVFile.withAbsoluteLocalPath(name + nameSuffix
                            , realPath);
                    avFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            // 成功或失败处理...
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
                break;
            default:
                break;
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
