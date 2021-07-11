package com.cmq.dex;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class V24 {
    private static class ReplaceClassLoader extends ClassLoader {
        private final String mApplicationClassName;
        private final ClassLoader mOldClassLoader;

        private ClassLoader mNewClassLoader;

        private final ThreadLocal<Boolean> mCallFindClassOfLeafDirectly = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return false;
            }
        };

        ReplaceClassLoader(String applicationClassName, ClassLoader oldClassLoader) {
            super(ClassLoader.getSystemClassLoader());
            mApplicationClassName = applicationClassName;
            mOldClassLoader = oldClassLoader;
        }

        void setNewClassLoader(ClassLoader classLoader) {
            mNewClassLoader = classLoader;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            System.out.println("find:" + name);
            if (mCallFindClassOfLeafDirectly.get()) {
                return null;
            }
            // 1、Application类不需要修复，使用原本的类加载器获得
            if (name.equals(mApplicationClassName)) {
                return findClass(mOldClassLoader, name);
            }
            // 2、加载热修复框架的类 因为不需要修复，就用原本的类加载器获得
            if (name.startsWith("com.cmq.dex")) {
                return findClass(mOldClassLoader, name);
            }
            if(name.startsWith("com.alibaba.android.arouter"))
                return findClass(mOldClassLoader,name);
            try {
                return findClass(mNewClassLoader, name);
            } catch (ClassNotFoundException ignored) {
                return findClass(mOldClassLoader, name);
            }
        }

        private Class<?> findClass(ClassLoader classLoader, String name) throws ClassNotFoundException {
            try {
                //双亲委托，所以可能会stackoverflow死循环，防止这个情况
                mCallFindClassOfLeafDirectly.set(true);
                return classLoader.loadClass(name);
            } finally {
                mCallFindClassOfLeafDirectly.set(false);
            }
        }
    }

    public static void install(Context context, ClassLoader classLoader,  ArrayList<File> list) throws Exception {
        Field pathListField = ReflectUtil.findField(classLoader, "pathList");
        Object pathList = pathListField.get(classLoader);

        Field dexElementsField = ReflectUtil.findField(pathList, "dexElements");
        Object[] dexElements = (Object[]) dexElementsField.get(pathList);

        Field dexFileField = ReflectUtil.findField(dexElements[0], "dexFile");

        StringBuilder dexPathBuilder = new StringBuilder();
        String packageName = context.getPackageName();
        boolean first = true;
        for (File file : list) {
            if (first) first = false;
            else dexPathBuilder.append(File.separator);
            dexPathBuilder.append(file.getAbsolutePath());
        }
        for (Object dexElement : dexElements) {
            String dexPath = null;
            DexFile dexFile = (DexFile) dexFileField.get(dexElement);
            if (dexFile != null) {
                dexPath = dexFile.getName();
            }
            if (dexPath == null || dexPath.isEmpty()) {
                continue;
            }
            if (!dexPath.contains("/" + packageName)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                dexPathBuilder.append(File.pathSeparator);
            }
            dexPathBuilder.append(dexPath);
        }

        //  app的native库（so） 文件目录 用于构造classloader
        Field nativeLibraryDirectoriesField = ReflectUtil.findField(pathList, "nativeLibraryDirectories");
        List<File> oldNativeLibraryDirectories = (List<File>) nativeLibraryDirectoriesField.get(pathList);


        StringBuilder libraryPathBuilder = new StringBuilder();
        first = true;
        for (File libDir : oldNativeLibraryDirectories) {
            if (libDir == null) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                libraryPathBuilder.append(File.pathSeparator);
            }
            libraryPathBuilder.append(libDir.getAbsolutePath());
        }
        ReplaceClassLoader rcl = new ReplaceClassLoader(context.getClass().getName(),classLoader);

        PathClassLoader replaceClassLoader = new PathClassLoader( dexPathBuilder.toString(), libraryPathBuilder.toString(),rcl);
        rcl.setNewClassLoader(replaceClassLoader);

        ReflectUtil.findField(pathList, "definingContext").set(pathList, replaceClassLoader);
        ReflectUtil.findField(replaceClassLoader, "parent").set(replaceClassLoader, rcl);

        //替换主线程的默认加载器
        Thread.currentThread().setContextClassLoader(replaceClassLoader);

        //替换ContextImp里的mClassLoader这是控件以及四大组件的加载器
        Context baseContext = (Context) ReflectUtil.findField(context, "mBase").get(context);
        Object basePackageInfo = ReflectUtil.findField(baseContext, "mPackageInfo").get(baseContext);
        ReflectUtil.findField(basePackageInfo, "mClassLoader").set(basePackageInfo, replaceClassLoader);
        //加载资源类的加载器
        if (Build.VERSION.SDK_INT < 27) {
            Resources res = context.getResources();
            try {
                ReflectUtil.findField(res, "mClassLoader").set(res, classLoader);
                final Object drawableInflater = ReflectUtil.findField(res, "mDrawableInflater").get(res);
                if (drawableInflater != null) {
                    ReflectUtil.findField(drawableInflater, "mClassLoader").set(drawableInflater, replaceClassLoader);
                }
            } catch (Throwable ignored) {
                // Ignored.
            }
        }

    }
}
