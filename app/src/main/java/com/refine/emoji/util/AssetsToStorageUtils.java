package com.refine.emoji.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Refine on 2018/6/20/020.
 */

public class AssetsToStorageUtils {

    public static final String LANGUAGE_CHI_SIM = "chi_sim.traineddata";

    private Context mContext;

    public AssetsToStorageUtils(Context context) {
        mContext = context;
    }

    public void assetsToStorage(String dir, String language) {
        //boolean mkdirs = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tessdata")
        //        .mkdirs();

        String path = dir + File.separator + language;

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
            inputStream = mContext.getAssets().open(language);
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
}
