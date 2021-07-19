//
// Created by cuimingqiang on 2021/7/14.
//

#include "Channel.h"

void *taskDecodePacket(void *args) {
    auto channel = static_cast<Channel *>(args);
    channel->decode();
    return nullptr;
}

void *taskPlay(void *args) {
    auto channel = static_cast<Channel *>(args);
    channel->play();
    return nullptr;
}

void Channel::start() {
    isPlay = true;
    packets.startWork();
    frames.startWork();
    pthread_create(&decodeThread, nullptr, taskDecodePacket, this);
    pthread_create(&playThread, nullptr, taskPlay, this);
}

void Channel::stop() {
    pthread_join(playThread, nullptr);
    pthread_join(decodeThread, nullptr);
    isPlay = false;
    packets.stopWork();
    frames.stopWork();
    packets.clear();
    frames.clear();
}

void Channel::decode() {
    AVPacket *packet = nullptr;
    while (isPlay) {
        if (isPlay && !canPushFrame()) {
            av_usleep(10 * 1000);
            continue;
        }
        int ret = packets.consume(packet);
        if (!ret) {
            //向缓冲区发送数据，返回非0的数据表示失败
            if (avcodec_send_packet(avCodecContext, packet))break;
        }
        auto frame = av_frame_alloc();
        ret = avcodec_receive_frame(avCodecContext, frame);
        av_packet_unref(packet);
        releasePacket(&packet);
        if (ret == 0) {
            frames.produce(frame);
        }else if (ret == AVERROR(EAGAIN)) {
            continue;
        } else {//解析失败
            if (frame)releaseFrame(&frame);
            break;
        }
    }
    av_packet_unref(packet);
    releasePacket(&packet);
}

void Channel::producePacket(AVPacket *packet) {
    packets.produce(packet);
}

bool Channel::canPushPackets() {
    return packets.size() <= 100;
}

bool Channel::canPushFrame() {
    return frames.size() <= 100;
}

bool Channel::emptyPackets() {
    return packets.size() == 0;
}

bool Channel::acceptStream(int stream) {
    return this->streamIndex == stream;
}
