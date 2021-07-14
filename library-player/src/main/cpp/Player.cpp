//
// Created by cuimingqiang on 2021/7/14.
//

#include <cstring>
#include "Player.h"


Player::~Player() {
    if (datasource)
        delete datasource;
    if (holder) delete holder;
}

Player::Player(JavaHolder *holder) {
    this->holder = holder;
}

void Player::setDatasource(const char *path) {
    datasource = new char[strlen(path) + 1];
    strcpy(datasource, path);
}

void Player::prepare() {
    avFormatContext = avformat_alloc_context();

    AVDictionary *dictionary = nullptr;
    av_dict_set(&dictionary, "timeout", "10000000", 0);
    //打开媒体文件地址
    int result = avformat_open_input(&avFormatContext, datasource, nullptr, &dictionary);
    av_dict_free(&dictionary);
    if (result) {//打开播放器失败 0表示失败
        //通知上层 java
        holder->onError(0, "视频地址打开失败");
        avformat_free_context(avFormatContext);
        return;
    }
    //读取流
    result = avformat_find_stream_info(avFormatContext, nullptr);
    if (result < 0) {//失败
        holder->onError(1, "打开流失败");
        avformat_close_input(&avFormatContext);
        return;
    }
    this->duration = (int) avFormatContext->duration / AV_TIME_BASE;
    LOG_D("视频时长：%d", duration);

    AVCodecContext *avCodecContext = nullptr;
    for (int streamIndex = 0; streamIndex < avFormatContext->nb_streams; streamIndex++) {
        auto stream = avFormatContext->streams[streamIndex];
        auto parameters = stream->codecpar;
        auto codec = avcodec_find_decoder(parameters->codec_id);
        if (!codec) {
            holder->onError(2, "找不到解码器");
            avformat_close_input(&avFormatContext);
            return;
        }
        avCodecContext = avcodec_alloc_context3(codec);
        if (!avCodecContext) {
            holder->onError(3, "解码器创建失败");
            avcodec_free_context(&avCodecContext);
            avformat_close_input(&avFormatContext);
            return;
        }
        result = avcodec_parameters_to_context(avCodecContext, parameters);
        if (result < 0) {
            holder->onError(4, "解码器参数配置失败");
            avcodec_free_context(&avCodecContext);
            avformat_close_input(&avFormatContext);
            return;
        }
        result = avcodec_open2(avCodecContext, codec, nullptr);
        if (result) {//
            holder->onError(5, "解码器打开失败");
            avcodec_free_context(&avCodecContext);
            avformat_close_input(&avFormatContext);
        }
        auto time_base = stream->time_base;
        if (parameters->codec_type == AVMEDIA_TYPE_AUDIO) {//音频

        } else if (parameters->codec_type == AVMEDIA_TYPE_VIDEO) {//视频
            if (stream->disposition & AV_DISPOSITION_ATTACHED_PIC)//封面
                continue;
            auto fps = stream->avg_frame_rate;
            videoChannel = new VideoChannel(streamIndex, avCodecContext, time_base,
                                            duration == 0 ? LIVE : VIDEO, holder,
                                            (int) av_q2d(fps));
        }
    }
}




