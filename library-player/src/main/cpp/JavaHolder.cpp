//
// Created by cuimingqiang on 2021/7/14.
//

#include <cstring>
#include "JavaHolder.h"
#include "util.h"

JavaHolder::JavaHolder(JavaVM *vm, JNIEnv *env, jweak player) {
    this->vm = vm;
    this->player = player;
    this->env = env;
    this->error = env->GetMethodID(reinterpret_cast<jclass>(player), "onError",
                                   "(I,java/lang/String)V");
}

JavaHolder::~JavaHolder() {
    vm = nullptr;
    player = nullptr;
    env->DeleteWeakGlobalRef(player);
}

void JavaHolder::onError(int code, const char *msg) {
    if (env){
        auto message = env->NewStringUTF(msg);
        env->CallVoidMethod(reinterpret_cast<jclass>(player), error, code, message);
    }else {
        JNIEnv *env;
        vm->AttachCurrentThread(&env, nullptr);
        auto message = env->NewStringUTF(msg);
        env->CallVoidMethod(reinterpret_cast<jclass>(player), error, code, message);
        vm->DetachCurrentThread();
    }
}