package com.cmq.dex;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;

public final class DexReplaceManager {

    public static void install(Context application, File patchPath) {
        ClassLoader classLoader = application.getClassLoader();
        File[] files = patchPath.listFiles();
        ArrayList<File> list = new ArrayList(files.length);
        for (File file : files) {
            if (file.getName().endsWith(".dex"))
                list.add(file);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                V24.install(application, classLoader, list);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    V23.install(classLoader,  list);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    V19.install(classLoader,  list); //4.4以上
                } else {  // >= 14
                    V14.install(classLoader,  list);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
