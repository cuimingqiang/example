//
// Created by cuimingqiang on 2021/7/14.
//

#ifndef EXAMPLE_CHANNEL_H
#define EXAMPLE_CHANNEL_H

#include "ConcurrentQueue.h"

#include "ffmpeg/include/libavcodec/avcodec.h"
#include "JavaHolder.h"

enum ChannelType{
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
    JavaHolder *holder;
public:
    Channel(int streamIndex, AVCodecContext *context, AVRational base, ChannelType type,JavaHolder *holder)
            : streamIndex(streamIndex),
            type(type),
              avCodecContext(context), timeBase(base), holder(holder) {
        packets.release(releasePacket);
        frames.release(releaseFrame);
    }

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