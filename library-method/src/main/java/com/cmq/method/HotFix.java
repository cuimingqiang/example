package com.cmq.method;

import java.lang.reflect.Method;

/**
 * https://developer.aliyun.com/article/74598#
 * https://github.com/tiann/FreeReflection.git
 */
public class HotFix {

    static {
        System.loadLibrary("method");
    }

    public native static void fixMethod(Method src,Method dest);
}
