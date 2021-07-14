//
// Created by cuimingqiang on 2021/7/14.
//

#ifndef EXAMPLE_JAVAHOLDER_H
#define EXAMPLE_JAVAHOLDER_H


#include "util.h"

class JavaHolder {
private:
    jweak player;
    JavaVM *vm;
    JNIEnv *env;
    jmethodID error;
public:
    JavaHolder(JavaVM *vm,JNIEnv *env, jweak player);

    ~JavaHolder();

    void onError(int code,const char *msg);
};


#endif //EXAMPLE_JAVAHOLDER_H
