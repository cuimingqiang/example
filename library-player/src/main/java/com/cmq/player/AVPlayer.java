package com.cmq.player;

public class AVPlayer {
    static {
        System.loadLibrary("player");
    }

    private long mNativePtr;

    public AVPlayer() {
        mNativePtr = nativeInit();
    }


    public void setDatasource(String path) {
        nativeDataSource(mNativePtr, path);
    }

    public void prepare(){
        nativePrepare(mNativePtr);
    }

    private void onError(int code,String msg){

    }

    private native long nativeInit();

    private native void nativeDataSource(long ptr, String path);

    private native void nativePrepare(long ptr);
}
