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

class RenderVideo {
public:
    virtual void render(uint8_t *src_data,int scaleWidth,int scaleHeight ,int width, int height, int ineSize) = 0;
    virtual int renderWidth() = 0;
    virtual int renderHeight() = 0;
};

class VideoChannel : public Channel {
private:
    int fps;
    RenderVideo *render;
    int renderWidth;
    int renderHeight;
public:
    VideoChannel(int streamIndex, AVCodecContext *context, AVRational base, ChannelType type,
                 JavaPlayerHolder *holder, int fps);

    ~VideoChannel();

    void setRender(RenderVideo *render);

    void play() override;
};


#endif //EXAMPLE_VIDEOCHANNEL_H
