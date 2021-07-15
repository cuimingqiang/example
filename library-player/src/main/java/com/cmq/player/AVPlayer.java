package com.cmq.player;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class AVPlayer implements SurfaceHolder.Callback {
    static {
        System.loadLibrary("player");
    }

    private long mNativePtr;
    private SurfaceHolder surfaceHolder;
    public AVPlayer() {
        mNativePtr = nativeInit();
    }

    public void setDatasource(String path) {
        nativeDataSource(mNativePtr, path);
    }

    public void setSurfaceView(SurfaceView surfaceView){
        if(surfaceHolder!=null){
            surfaceHolder.removeCallback(this);
        }
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        nativeSetSurfaceView(mNativePtr,holder.getSurface());
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    public void prepare() {
        nativePrepare(mNativePtr);
    }

    private void onPrepare(){
        Log.i("-----player","onPrepare");
    }

    private void onError(int code) {
        Log.e("---player", "code = " + code);
    }

    private native long nativeInit();

    private native void nativeDataSource(long ptr, String path);

    private native void nativePrepare(long ptr);

    private native void nativeSetSurfaceView(long ptr, Surface surface);
}
