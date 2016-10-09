package com.yezi.zuo.music_big_boom;

import android.util.Log;

/**
 * Created by zuo on 2016/10/9.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e("炸了",thread.getName()+ex.toString());
    }
}
