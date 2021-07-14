package com.cmq.demo.app;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.facade.template.ILogger;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cmq.skin.SkinManager;

public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        File dir = new File(getCacheDir(),"dex");
//        if (dir.exists())
//            DexReplaceManager.install(this, dir);
        ARouter.openDebug();
        ARouter.openLog();
        ARouter.init(this);
        ARouter.setLogger(new ILogger() {
            @Override
            public void showLog(boolean isShowLog) {

            }

            @Override
            public void showStackTrace(boolean isShowStackTrace) {

            }

            @Override
            public void debug(String tag, String message) {
                Log.d(tag,message);
            }

            @Override
            public void info(String tag, String message) {
                Log.i(tag,message);
            }

            @Override
            public void warning(String tag, String message) {
                Log.w(tag,message);
            }

            @Override
            public void error(String tag, String message) {
                Log.e(tag,message);
            }

            @Override
            public void error(String tag, String message, Throwable e) {
                Log.e(tag,message,e);
            }

            @Override
            public void monitor(String message) {
                Log.d("monitor",message);
            }

            @Override
            public boolean isMonitorMode() {
                return true;
            }

            @Override
            public String getDefaultTag() {
                return "aroute";
            }
        });
        SkinManager.init(this, map -> {

        });

    }
}
