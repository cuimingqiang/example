//
// Created by cuimingqiang on 2021/7/14.
//

#ifndef EXAMPLE_VIDEOCHANNEL_H
#define EXAMPLE_VIDEOCHANNEL_H
extern "C" {
#include <libswscale/swscale.h>
#include <libavutil/avutil.h>
#include <libavutil/imgutils.h>
};

#include "Channel.h"

class VideoChannel : Channel{
private:
    int fps;
public:
    VideoChannel(int streamIndex, AVCodecContext *context, AVRational base,ChannelType type, JavaPlayerHolder *holder,int fps);
};


#endif //EXAMPLE_VIDEOCHANNEL_H
