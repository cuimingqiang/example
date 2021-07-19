//
// Created by cuimingqiang on 2021/7/17.
//

#ifndef EXAMPLE_AUDIOCHANNEL_H
#define EXAMPLE_AUDIOCHANNEL_H

#include "Channel.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavutil/time.h>
#include <libswscale/swscale.h>
#include <libavutil/avutil.h>
#include <libavutil/imgutils.h>
};


class AudioChannel : public Channel{

public:
    AudioChannel(int streamIndex, AVCodecContext *context, AVRational base, ChannelType type,
                 JavaPlayerHolder *holder);
    ~ AudioChannel();

    void play() override;
};


#endif //EXAMPLE_AUDIOCHANNEL_H
