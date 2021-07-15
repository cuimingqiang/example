//
// Created by cuimingqiang on 2021/7/15.
//


#include "JavaPlayerHolder.h"


JavaPlayerHolder::JavaPlayerHolder(JavaVM *vm, JNIEnv *env, jweak player) {
    this->vm = vm;
    this->player = player;
    jclass clazz = env->GetObjectClass(reinterpret_cast<jobject>(player));
    this->error = env->GetMethodID(clazz, "onError", "(I)V");
    this->prepare = env->GetMethodID(clazz, "onPrepare", "()V");
    pthread_key_create(&tls, threadDestructor);
    pthread_setspecific(tls, env);
}

void JavaPlayerHolder::doOnEnv(jmethodID method, ...) {
    const pthread_key_t k = tls;
    auto env = static_cast<JNIEnv *>(pthread_getspecific(k));
    va_list params;
    va_start(params, method);
    if (env) {
        env->CallVoidMethod(reinterpret_cast<jobject>(player), method, params);
    } else {
        vm->AttachCurrentThread(&env, nullptr);
        env->CallVoidMethod(reinterpret_cast<jobject>(player), method, params);
        vm->DetachCurrentThread();
    }
    va_end(params);
}


void JavaPlayerHolder::threadDestructor(void *st) {

}

JavaPlayerHolder::~JavaPlayerHolder() {
    vm = nullptr;
    player = nullptr;
    const pthread_key_t k = tls;
    auto env = static_cast<JNIEnv *>(pthread_getspecific(k));
    if (env) {
        env->DeleteWeakGlobalRef(player);
    } else {
        vm->AttachCurrentThread(&env, nullptr);
        env->DeleteWeakGlobalRef(player);
        vm->DetachCurrentThread();
    }
}

void *chatToString(JNIEnv *env, void *src) {
    return env->NewStringUTF((char *)src);
}

void JavaPlayerHolder::onError(int code, const char *msg) {
   // jstring str = static_cast<jstring>(convert(chatToString, (void *) msg));
    doOnEnv(error, code);
}

void JavaPlayerHolder::onPrepare() {
    doOnEnv(prepare);
}

void *JavaPlayerHolder::convert(JavaPlayerHolder::Convert fun ,void *e) {
    const pthread_key_t k = tls;
    auto env = static_cast<JNIEnv *>(pthread_getspecific(k));
    void *result = nullptr;
    if (env) {
        result = fun(env, e);
    } else {
        vm->AttachCurrentThread(&env, nullptr);
        result = fun(env,e);
        vm->DetachCurrentThread();
    }
    return result;
}

