//
// Created by cuimingqiang on 2021/7/14.
//

#ifndef EXAMPLE_PLAYER_H
#define EXAMPLE_PLAYER_H

#include <jni.h>
#include "JavaPlayerHolder.h"
#include "VideoChannel.h"
#include "AudioChannel.h"
#include <android/native_window_jni.h>
extern "C" {
#include <libavformat/avformat.h>
#include <libavutil/time.h>
#include <libswscale/swscale.h>
#include <libavutil/avutil.h>
#include <libavutil/imgutils.h>
};

enum PlayState{
    init,play,pause,over,error
};

class Player : public RenderVideo{
private:
    //java层player对象
    JavaPlayerHolder *holder;
    char *datasource;
    AVFormatContext *avFormatContext = nullptr;
    //时长
    int duration;
    ANativeWindow* window = nullptr;
    VideoChannel * videoChannel;
    AudioChannel * audioChannel;
    pthread_mutex_t windowMutex = PTHREAD_MUTEX_INITIALIZER;
    PlayState state = init;

    pthread_t prepareThread;
    pthread_t parseThread;

    int windowWidth;
    int windowHeight;

public:
    Player(JavaPlayerHolder *holder);

    ~Player();

    void setDatasource(const char *path);

    void prepare();

    void start();

    void stop();

    void setWindow(JNIEnv *env,jobject surface );

    void render(uint8_t *src_data,int scaleWidth,int scaleHeight ,int width, int height, int lineSize) override;
    bool syncAudio(double delay, double videoTime) override;
    int renderHeight() override;
    int renderWidth() override;
public:
    void prepareDatasource();
    void parseDatasource();

};


#endif //EXAMPLE_PLAYER_H
