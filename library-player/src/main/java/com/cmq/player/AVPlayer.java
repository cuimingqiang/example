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

    public interface OnPreparedListener {
        void onPrepared();
    }

    private long mNativePtr;
    private SurfaceHolder surfaceHolder;
    private OnPreparedListener preparedListener;

    public AVPlayer() {
        mNativePtr = nativeInit();
    }

    public void setOnPreparedListener(OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }

    public void setDatasource(String path) {
        nativeDataSource(mNativePtr, path);
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        Log.i("-----player","setSurfaceView");
        if (surfaceHolder != null) {
            surfaceHolder.removeCallback(this);
        }
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.i("-----player","surfaceCreated");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.i("----player", "surface == " + (holder.getSurface() == null));
        nativeSetSurfaceView(mNativePtr, holder.getSurface());
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    public void start() {
        nativeStart(mNativePtr);
    }

    public void stop() {
        nativeStop(mNativePtr);
    }

    public void release() {
        nativeRelease(mNativePtr);
    }

    public void prepare() {
        nativePrepare(mNativePtr);
    }

    private void onPrepare() {
        Log.i("-----player", "onPrepare");
        if (preparedListener != null) preparedListener.onPrepared();
    }

    private void onError(int code) {
        Log.e("---player", "code = " + code);
    }

    private native long nativeInit();

    private native void nativeDataSource(long ptr, String path);

    private native void nativePrepare(long ptr);

    private native void nativeSetSurfaceView(long ptr, Surface surface);

    private native void nativeStart(long ptr);

    private native void nativeStop(long ptr);

    private native void nativeRelease(long ptr);
}
