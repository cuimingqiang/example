package com.cmq.av;

public class AVPlayer {

    private long mNativePtr;

    private native long nativeInit();

    private native void nativeDataSource(long ptr,String path);

    public void setDatasource(String path){
        nativeDataSource(mNativePtr,path);
    }
}
