package com.refine.emoji;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.refine.emoji.util.LogUtils;

/**
 * Created by Refine on 2018/5/17/017.
 */

public class App extends Application {

    private static final String APP_ID = "aTTuIKJjAhBXk1Pjox61RYUm-gzGzoHsz";
    private static final String APP_KEY = "reQByAeoBSC4tuTN2q5AhDzc";

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        LogUtils.init("", true);

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, APP_ID, APP_KEY);

        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);

        AVOSCloud.setCacheFileAutoExpireDate(7);
    }
}
