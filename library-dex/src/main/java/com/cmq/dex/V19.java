package com.cmq.dex;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class V19 {
    public static void install(ClassLoader classLoader,  ArrayList<File> list)throws Exception {
        Field pathListField = ReflectUtil.findField(classLoader, "pathList");
        Object dexPathList = pathListField.get(classLoader);

        ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
        Object[] patchElements = makeDexElements(dexPathList,new ArrayList<File>(list), null, suppressedExceptions);

        ReflectUtil.expandFieldArray(dexPathList, "dexElements", patchElements);

        if (suppressedExceptions.size() > 0) {
            for (IOException e : suppressedExceptions) {

                throw e;
            }
        }
    }
    private static Object[] makeDexElements(
            Object dexPathList, ArrayList<File> files, File optimizedDirectory,
            ArrayList<IOException> suppressedExceptions)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Method makeDexElements = ReflectUtil.findMethod(dexPathList, "makeDexElements",
                ArrayList.class, File.class,
                ArrayList.class);


        return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory,
                suppressedExceptions);
    }
}
