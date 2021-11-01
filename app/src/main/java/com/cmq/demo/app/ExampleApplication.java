package com.cmq.demo.app;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.facade.template.ILogger;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cmq.demo.main.MainActivity;
import com.cmq.method.HotFix;
import com.cmq.method.Test;
import com.cmq.skin.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        File dir = new File(getCacheDir(),"dex");
//        if (dir.exists())
//            DexReplaceManager.install(this, dir);

        try {
            InputStream is = getResources().getAssets().open("test.dex");
            byte[] buffer = new byte[2024];
            File dir = new File(getCacheDir(),"dex");
            if(!dir.exists())dir.mkdirs();
            File repair = new File(dir,"test.dex");
            if(!repair.exists()) {
                repair.createNewFile();
                FileOutputStream fos = new FileOutputStream(repair);
                int length = -1;
                while ((length = is.read(buffer))!=-1){
                    fos.write(buffer,0,length);
                }
                is.close();
                fos.close();
            }
            Method print = Test.class.getDeclaredMethod("print", null);
            DexFile df = new DexFile(repair);
            Class<?> aClass = df.loadClass("com.cmq.method.Test",null);
            Method appPrint = aClass.getDeclaredMethod("print", null);
            HotFix.fixMethod(print,appPrint);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public void print(){
        Log.i("---hot fix -->","success");
    }
}
