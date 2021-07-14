package com.cmq.demo.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.alibaba.android.arouter.facade.template.ILogger;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cmq.dex.DexReplaceManager;
import com.cmq.method.HotFix;
import com.cmq.skin.AttrProcessor;
import com.cmq.skin.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        File dir = new File(getCacheDir(),"dex");
//        if (dir.exists())
//            DexReplaceManager.install(this, dir);
        ARouter.openDebug();
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

            }

            @Override
            public void info(String tag, String message) {

            }

            @Override
            public void warning(String tag, String message) {

            }

            @Override
            public void error(String tag, String message) {

            }

            @Override
            public void error(String tag, String message, Throwable e) {
                Log.e(tag,message,e);
            }

            @Override
            public void monitor(String message) {

            }

            @Override
            public boolean isMonitorMode() {
                return false;
            }

            @Override
            public String getDefaultTag() {
                return "aroute";
            }
        });
        SkinManager.init(this, map -> {

        });
        try {
            Method method = getClass().getDeclaredMethod("onCreate");
            Method onCreate = getClass().getSuperclass().getDeclaredMethod("onCreate");


            HotFix.fixMethod(method,onCreate);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
