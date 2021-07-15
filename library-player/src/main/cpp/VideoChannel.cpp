//
// Created by cuimingqiang on 2021/7/14.
//

#include "VideoChannel.h"

VideoChannel::VideoChannel(int streamIndex, AVCodecContext *context, AVRational base,ChannelType type,
                           JavaPlayerHolder *holder, int fps) : Channel(streamIndex, context, base,type,
                                                                  holder), fps(fps) {

}
