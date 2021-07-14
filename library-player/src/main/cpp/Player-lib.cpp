#include <jni.h>
#include "util.h"
#include "Player.h"
//
// Created by cuimingqiang on 2021/7/14.
//

extern "C" {
#include <libavutil/avutil.h>
}

JavaVM * vm;

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    ::vm = vm;
    auto version = av_version_info();
    LOG_D("jni --> JNI_OnLoad FFMpeg version --> %s" ,version);
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOG_D("jni --> JNI_OnUnload");
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_cmq_player_AVPlayer_nativeInit(JNIEnv *env, jobject thiz) {
    auto javaPlayer = env->NewWeakGlobalRef(thiz);
    auto player = new Player(new JavaHolder(vm,env,javaPlayer));
    return reinterpret_cast<jlong>(player);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cmq_player_AVPlayer_nativeDataSource(JNIEnv *env, jobject thiz, jlong ptr, jstring path) {
    auto player = reinterpret_cast<Player *>(ptr);
    auto url = env->GetStringUTFChars(path, nullptr);
    player->setDatasource(url);
    env->ReleaseStringUTFChars(path,url);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cmq_player_AVPlayer_nativePrepare(JNIEnv *env, jobject thiz, jlong ptr) {
    auto player = reinterpret_cast<Player *>(ptr);
    player->prepare();
}