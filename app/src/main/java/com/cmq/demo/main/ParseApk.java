package com.cmq.demo.main;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.TreeMap;

public class ParseApk {

    public static void parse(Context application) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TreeMap<String, ActivityInfo> map = new TreeMap<>();
                try {
                    Class clazz = Class.forName("android.content.pm.PackageParser");
                    Class activityClazz = Class.forName("android.content.pm.PackageParser$Activity");
                    Object object = clazz.newInstance();
                    Log.i("---object-->", object == null ? "null" : object.toString());
                    File pluginDir = new File(application.getCacheDir(), "plugin");
                    if (pluginDir.exists()) {
                    } else {
                        pluginDir.mkdirs();
                    }
                    File plugin = new File(pluginDir, "plugin.apk");
                    if (plugin.exists()) {
                    } else {
                        plugin.createNewFile();
                        InputStream inputStream = application.getResources().getAssets().open("plugin.apk");
                        FileOutputStream fos = new FileOutputStream(plugin);
                        byte[] buffer = new byte[2048];
                        int position = -1;
                        while ((position = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, position);
                        }
                        inputStream.close();
                        fos.close();
                    }
                    Method parsePackage = clazz.getDeclaredMethod("parsePackage", File.class, int.class);
                    parsePackage.setAccessible(true);

                    Object packageObject = parsePackage.invoke(object, new Object[]{plugin, 4});
                    Field applicationInfoField = packageObject.getClass().getField("applicationInfo");
                    ApplicationInfo applicationInfo = (ApplicationInfo) applicationInfoField.get(packageObject);
                    applicationInfo.sourceDir = plugin.getPath();
                    applicationInfo.nativeLibraryDir = pluginDir.getPath();


                    Field activities = packageObject.getClass().getField("activities");
                    Field receivers = packageObject.getClass().getField("receivers");
                    Field providers = packageObject.getClass().getField("providers");
                    Field services = packageObject.getClass().getField("services");
                    List<Object> listActivity = (List<Object>) activities.get(packageObject);

                    for (Object o : listActivity) {
                        Field info = activityClazz.getField("info");
                        ActivityInfo ai = (ActivityInfo) info.get(o);
                        if (ai.theme == 0) ai.theme = applicationInfo.theme;
                        Field className = activityClazz.getField("className");
                        String cn = (String) className.get(o);
                        map.put(cn, ai);
                    }
                    Log.i("---", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
