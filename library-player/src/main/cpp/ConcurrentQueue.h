//
// Created by cuimingqiang on 2021/7/14.
//

#ifndef EXAMPLE_CONCURRENTQUEUE_H
#define EXAMPLE_CONCURRENTQUEUE_H

#include <queue>

using namespace std;

template<typename T>
class ConcurrentQueue {
    typedef void (*ReleaseProvider)(T *);

private:
    queue<T> queue;
    bool isWork;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
    ReleaseProvider releaseProvider;
public:
    ConcurrentQueue();

    ~ ConcurrentQueue();

    void release(ReleaseProvider release);

    void startWork();

    void stopWork();

    void produce(T data);

    T consume();
};


template<typename T>
ConcurrentQueue<T>::ConcurrentQueue() {
    pthread_mutex_init(&mutex, 0);
    pthread_cond_init(&cond, 0);
    isWork = true;
}

template<typename T>
ConcurrentQueue<T>::~ConcurrentQueue() {
    pthread_mutex_destroy(&mutex);
    pthread_cond_destroy(&cond);
}

template<typename T>
void ConcurrentQueue<T>::produce(T data) {
    pthread_mutex_lock(&mutex);
    if (isWork) {
        queue.push(data);
        pthread_cond_signal(&cond);
    } else {//释放数据
        if (releaseProvider)releaseProvider(data);
    }
    pthread_mutex_unlock(&mutex);
}

template<typename T>
T ConcurrentQueue<T>::consume() {
    pthread_mutex_lock(&mutex);
    while (isWork && queue.empty()) {
        pthread_cond_wait(&cond, &mutex);
    }
    if (!queue.empty()) {
        T data = queue.front();
        queue.pop();
        return data;
    }
    pthread_mutex_unlock(&mutex);
    return nullptr;
}

template<typename T>
void ConcurrentQueue<T>::release(ConcurrentQueue::ReleaseProvider release) {
    this->releaseProvider = release;
}

template<typename T>
void ConcurrentQueue<T>::startWork() {
    isWork = true;
}

template<typename T>
void ConcurrentQueue<T>::stopWork() {
    isWork = false;
}

#endif //EXAMPLE_CONCURRENTQUEUE_H
