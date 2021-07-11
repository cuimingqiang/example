package com.cmq.dex;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class V23 {
    public static void install(ClassLoader classLoader,  ArrayList<File> list) throws Exception{
        //找到 pathList
        Field pathListField = ReflectUtil.findField(classLoader, "pathList");
        Object dexPathList = pathListField.get(classLoader);

        ArrayList<IOException> suppressedExceptions = new ArrayList<>();
        // 从 pathList找到 makePathElements 方法并执行
        // 得到补丁创建的 Element[]
        Object[] patchElements = makePathElements(dexPathList, new ArrayList<>(list), null, suppressedExceptions);

        //将原本的 dexElements 与 makePathElements生成的数组合并
        ReflectUtil.expandFieldArray(dexPathList, "dexElements", patchElements);
        if (suppressedExceptions.size() > 0) {
            for (IOException e : suppressedExceptions) {

                throw e;
            }

        }

    }

    private static Object[] makePathElements(
            Object dexPathList, ArrayList<File> files, File optimizedDirectory,
            ArrayList<IOException> suppressedExceptions)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //通过阅读android6、7、8、9源码，都存在makePathElements方法
        Method makePathElements = ReflectUtil.findMethod(dexPathList, "makePathElements",
                List.class, File.class,
                List.class);
        return (Object[]) makePathElements.invoke(dexPathList, files, optimizedDirectory,
                suppressedExceptions);
    }
}
