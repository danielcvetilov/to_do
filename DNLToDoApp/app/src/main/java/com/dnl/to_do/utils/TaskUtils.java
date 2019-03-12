package com.dnl.to_do.utils;

import android.os.Handler;
import android.os.Looper;

public class TaskUtils {
    public static void postOnMainAfter(int delay, Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
    }
}
