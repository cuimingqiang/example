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