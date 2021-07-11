//
// Created by cuimingqiang on 5/31/21.
//
#include <jni.h>
#include <android/log.h>

#define TAG "method-log"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *unused) {

    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cmq_method_HotFix_fixMethod(JNIEnv *env, jclass clazz, jobject src, jobject dest) {
    auto jid =  env->FromReflectedMethod(src);
    auto jdid =  env->FromReflectedMethod(dest);

    LOGD("大小%d || %d",sizeof(jid),sizeof(jdid));
    LOGD("大小%d || %d",sizeof(jdid),sizeof(&jdid));
}