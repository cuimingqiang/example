package com.cmq.method;

import java.lang.reflect.Method;

public class HotFix {

    static {
        System.loadLibrary("method");
    }

    public native static void fixMethod(Method src,Method dest);
}
