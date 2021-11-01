//
// Created by cuimingqiang on 2021/7/17.
//

#ifndef EXAMPLE_AUDIOCHANNEL_H
#define EXAMPLE_AUDIOCHANNEL_H

#include "Channel.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
extern "C" {
#include <libswresample/swresample.h>
};


class AudioChannel : public Channel{

private:
    int out_channels;
    int out_sample_size;
    int out_sample_rate;
    int out_buffers_size;
    SwrContext *swr_ctx = 0;

    double audio_time; //音视频同步
private:
    //引擎
    SLObjectItf engineObject = nullptr;
    // 引擎接口
    SLEngineItf engineInterface = nullptr;
    // 混音器
    SLObjectItf outputMixObject = nullptr;
    // 播放器
    SLObjectItf bqPlayerObject = nullptr;
    // 播放器接口
    SLPlayItf bqPlayerPlay = nullptr;

    // 播放器队列接口
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue = nullptr;
public:
    AudioChannel(int streamIndex, AVCodecContext *context, AVRational base, ChannelType type,
                 JavaPlayerHolder *holder);
    ~ AudioChannel();

    void play() override;
    void stop() override;
    double getAudioTime();
    int getPCM();

    uint8_t *out_buffers = nullptr;
};


#endif //EXAMPLE_AUDIOCHANNEL_H
