//
// Created by cuimingqiang on 5/28/21.
//

#include "util.h"
#include "AVPlayer.h"
extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOG_D("jni --> JNI_OnLoad");
    auto player = new AVPlayer;

    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOG_D("jni --> JNI_OnUnload");
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_cmq_av_AVPlayer_nativeInit(JNIEnv *env, jobject thiz) {
    auto player = new AVPlayer();
    return player;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cmq_av_AVPlayer_nativeDataSource(JNIEnv *env, jobject thiz, jlong ptr, jstring path) {

}