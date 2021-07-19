//
// Created by cuimingqiang on 2021/7/17.
//

#include "AudioChannel.h"

AudioChannel::AudioChannel(int streamIndex, AVCodecContext *context, AVRational base,
                           ChannelType type, JavaPlayerHolder *holder) : Channel(
        streamIndex, context, base, type, holder) {

}

AudioChannel::~AudioChannel() {

}

void AudioChannel::play() {

}


