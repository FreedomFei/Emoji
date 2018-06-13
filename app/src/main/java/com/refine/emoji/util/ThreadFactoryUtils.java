package com.refine.emoji.util;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Refine on 2018/6/3/003.
 */

public class ThreadFactoryUtils implements ThreadFactory {

    private final int mThreadPriority;

    public ThreadFactoryUtils(int threadPriority) {
        mThreadPriority = threadPriority;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(mThreadPriority);
                } catch (Throwable t) {

                }
                runnable.run();
            }
        };
        return new Thread(wrapperRunnable);
    }
}
