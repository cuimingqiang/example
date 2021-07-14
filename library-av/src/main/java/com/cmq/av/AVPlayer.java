package com.cmq.av;

public class AVPlayer {

    static {
//        System.loadLibrary("avcodec");
//        System.loadLibrary("avfilter");
//        System.loadLibrary("avformat");
//        System.loadLibrary("avutil");
//        System.loadLibrary("swresample");
//        System.loadLibrary("swscale");
        System.loadLibrary("av");
    }

    private long mNativePtr;

    public AVPlayer() {
        mNativePtr = nativeInit();
    }


    public void setDatasource(String path) {
        nativeDataSource(mNativePtr, path);
    }


    private native long nativeInit();

    private native void nativeDataSource(long ptr, String path);
}
