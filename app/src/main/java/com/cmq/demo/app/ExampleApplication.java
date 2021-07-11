package com.cmq.demo.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

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
        ARouter.init(this);
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
