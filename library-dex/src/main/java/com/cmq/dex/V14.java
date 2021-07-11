package com.cmq.dex;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class V14 {
    public static void install(ClassLoader classLoader, ArrayList<File> list) throws Exception {
        Field pathListField = ReflectUtil.findField(classLoader, "pathList");
        Object dexPathList = pathListField.get(classLoader);

        ReflectUtil.expandFieldArray(dexPathList, "dexElements",
                makeDexElements(dexPathList,
                        new ArrayList<File>(list), null));
    }

    private static Object[] makeDexElements(
            Object dexPathList, ArrayList<File> files, File optimizedDirectory)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Method makeDexElements =
                ReflectUtil.findMethod(dexPathList, "makeDexElements", ArrayList.class,
                        File.class);
        return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory);
    }
}
