//
// Created by cuimingqiang on 2021/7/14.
//

#ifndef EXAMPLE_PLAYER_H
#define EXAMPLE_PLAYER_H

#include <jni.h>
#include "JavaHolder.h"
#include "VideoChannel.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavutil/time.h>
};

class Player {
private:
    //java层player对象
    JavaHolder *holder;
    char *datasource;
    AVFormatContext *avFormatContext = nullptr;
    //时长
    int duration;
    VideoChannel * videoChannel;
public:
    Player(JavaHolder *holder);

    ~Player();

    void setDatasource(const char *path);

    void prepare();


};


#endif //EXAMPLE_PLAYER_H
