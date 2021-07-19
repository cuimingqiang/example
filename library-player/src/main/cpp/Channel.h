//
// Created by cuimingqiang on 2021/7/14.
//

#ifndef EXAMPLE_CHANNEL_H
#define EXAMPLE_CHANNEL_H

#include "ConcurrentQueue.h"

#include "JavaPlayerHolder.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavutil/time.h>
#include <libswscale/swscale.h>
#include <libavutil/avutil.h>
#include <libavutil/imgutils.h>
};

enum ChannelType {
    LIVE,
    VIDEO
};

class Channel {
protected:
    int streamIndex;
    ConcurrentQueue<AVPacket *> packets;
    ConcurrentQueue<AVFrame *> frames;
    AVCodecContext *avCodecContext;
    AVRational timeBase;
    ChannelType type;
    JavaPlayerHolder *holder;
    pthread_t decodeThread;
    pthread_t playThread;
    bool isPlay;
public:
    Channel(int streamIndex, AVCodecContext *context, AVRational base, ChannelType type,
            JavaPlayerHolder *holder)
            : streamIndex(streamIndex),
              type(type),
              avCodecContext(context), timeBase(base), holder(holder) {
        packets.release(releasePacket);
        frames.release(releaseFrame);
    }

    ~Channel() {
        if (holder)
            holder = nullptr;
        if (avCodecContext)
            avCodecContext = nullptr;

    }

    virtual bool acceptStream(int stream);
    virtual bool emptyPackets();
    virtual bool canPushPackets();
    virtual bool canPushFrame();
    virtual void producePacket(AVPacket * packet);
    virtual void start();
    virtual void stop();

    virtual void decode();
    virtual void play() = 0;

    static void releasePacket(AVPacket **packet) {
        if (packet) {
            av_packet_free(packet);
            *packet = nullptr;
        }
    }

    static void releaseFrame(AVFrame **frame) {
        if (frame) {
            av_frame_free(frame);
            *frame = nullptr;
        }
    }

};


#endif //EXAMPLE_CHANNEL_H
