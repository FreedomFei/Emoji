package com.refine.emoji;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.refine.emoji.base.BaseActivity;
import com.refine.emoji.base.recyclerview.BaseRVAdapter;
import com.refine.emoji.base.recyclerview.BaseViewHolder;
import com.refine.emoji.util.LogUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final int PICK_IMAGE = 100;

    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private EmojiAdapter homeMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);

        fab.setOnClickListener(this);

        loadEmoji();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                break;
            default:
                break;
        }
    }

    /**
     * /data/data/com.refine.emoji/cache/image_manager_disk_cache
     */
    private void testFileProvider() {
        File cacheDir = getCacheDir();
        File file = cacheDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "image_manager_disk_cache".equals(name);
            }
        })[0];
        LogUtils.d(file.getAbsolutePath());
    }

    private void loadEmoji() {
        // 测试 SDK 是否正常工作的代码
        //AVObject testObject = new AVObject("TestObject");
        //testObject.put("words", "Hello World!");
        //testObject.saveInBackground(new SaveCallback() {
        //    @Override
        //    public void done(AVException e) {
        //        if (e == null) {
        //            LogUtil.log.d("saved", "success!");
        //        }
        //    }
        //});

        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.orderByDescending(AVObject.CREATED_AT);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        //设置为一天，单位毫秒
        query.setMaxCacheAge(24 * 3600 * 1000);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                List<AVFile> avFiles = new ArrayList<>();
                if (e != null) {
                    toast(e.getMessage());
                }

                if (list == null || list.isEmpty()) {
                    return;
                }

                for (AVObject avObject : list) {
                    LogUtil.log.e("xxx", avObject.toString());

                    avFiles.add(AVFile.withAVObject(avObject));
                }

                homeMessageAdapter = new EmojiAdapter(MainActivity.this, avFiles);
                recyclerView.setAdapter(homeMessageAdapter);
                homeMessageAdapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener<AVFile>() {

                    @Override
                    public void onItemClick(List<AVFile> data, View view, int position, long id) {
                        AVFile avFile = data.get(position);

                        showDialog(avFile, position);
                    }
                });
            }
        });
    }

    private void showDialog(final AVFile avFile, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("选择需要的操作！");
        //正
        builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                send(avFile);
            }
        });
        //中
        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(avFile, position);
            }
        });
        //负
        builder.setNegativeButton("编辑", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String objectId = avFile.getObjectId();
                String name = nameFormat(avFile);
                String url = avFile.getUrl();

                Intent intent = new Intent(MainActivity.this, PreviewImageActivity.class);
                intent.putExtra("id", objectId);
                intent.putExtra("name", name);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    private void send(AVFile avFile) {
        try {
            GlideApp.with(MainActivity.this)
                    .asFile()
                    .load(avFile.getUrl())
                    .into(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.refine.emoji.fileprovider", resource);

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            shareIntent.setType("image/*");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(shareIntent, "tt"));
                        }
                    });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void delete(AVFile avFile, final int position) {
        avFile.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    homeMessageAdapter.remove(position);
                    toast("删除成功");
                } else {
                    toast(e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            data.setClass(this, PreviewImageActivity.class);
            startActivity(data);
        }
    }

    class EmojiAdapter extends BaseRVAdapter {

        private Context mContext;
        private List<AVFile> mList;

        public EmojiAdapter(Context context, List<AVFile> list) {
            super(context, list);
            mContext = context;
            mList = list;
        }

        @Override
        public int onCreateViewLayoutID(int viewType) {
            return R.layout.item_emoji;
        }

        @Override
        public void onBindViewHolder(final BaseViewHolder holder, int position) {
            ImageView ivEmoji = holder.getImageView(R.id.iv_emoji);
            TextView tvEmojiTitle = holder.getTextView(R.id.tv_emoji_title);
            TextView tvEmojiDescribe = holder.getTextView(R.id.tv_emoji_describe);

            AVFile avFile = mList.get(position);
            GlideApp.with(MainActivity.this)
                    .load(avFile.getUrl())
                    //忽大忽小、闪烁问题
                    .override(Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivEmoji);

            tvEmojiTitle.setText(nameFormat(avFile));
            tvEmojiDescribe.setText(String.valueOf(avFile.getSize()));
        }
    }

    private String nameFormat(AVFile avFile) {
        String fileName = String.valueOf(avFile.getMetaData("_name"));
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }
}
