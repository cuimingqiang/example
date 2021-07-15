//
// Created by cuimingqiang on 2021/7/15.
//

#ifndef EXAMPLE_JAVAPLAYERHOLDER_H
#define EXAMPLE_JAVAPLAYERHOLDER_H

#include <pthread.h>
#include "util.h"

class JavaPlayerHolder {
private:
    jweak player;
    JavaVM *vm;
    jmethodID error;
    jmethodID prepare;
    pthread_key_t tls = 0;
public:
    JavaPlayerHolder(JavaVM *vm, JNIEnv *env, jweak player);

    ~JavaPlayerHolder();

    void onError(int code, const char *msg);

    void onPrepare();

private:
    typedef void *(*Convert)(JNIEnv *env, void *);

    static void threadDestructor(void *st);

    void doOnEnv(jmethodID method, ...);

    void *convert(Convert c, void *src);
};


#endif //EXAMPLE_JAVAPLAYERHOLDER_H
