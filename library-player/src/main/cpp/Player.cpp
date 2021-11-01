//
// Created by cuimingqiang on 2021/7/14.
//

#include <cstring>
#include "Player.h"


Player::~Player() {
    delete datasource;
    delete holder;
    delete videoChannel;
    delete audioChannel;
}

Player::Player(JavaPlayerHolder *holder) : windowWidth(0), windowHeight(0) {
    this->holder = holder;
}

void Player::setDatasource(const char *path) {
    datasource = new char[strlen(path) + 1];
    strcpy(datasource, path);
}

void *prepareFFmpegContext(void *st) {
    auto p = static_cast<Player *>(st);
    p->prepareDatasource();
    return nullptr;
}

void Player::prepare() {
    pthread_create(&prepareThread, nullptr, prepareFFmpegContext, this);
}

void Player::prepareDatasource() {
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
            audioChannel = new AudioChannel(streamIndex, avCodecContext, time_base,
                                            duration == 0 ? LIVE : VIDEO, holder);
        } else if (parameters->codec_type == AVMEDIA_TYPE_VIDEO) {//视频
            if (stream->disposition & AV_DISPOSITION_ATTACHED_PIC)//封面
                continue;
            auto fps = stream->avg_frame_rate;
            videoChannel = new VideoChannel(streamIndex, avCodecContext, time_base,
                                            duration == 0 ? LIVE : VIDEO, holder,
                                            (int) av_q2d(fps));
            videoChannel->setRender(this);
        }
    }
    if (!videoChannel) {
        if (avCodecContext)avcodec_free_context(&avCodecContext);
        avformat_close_input(&avFormatContext);
    }
    holder->onPrepare();
}

int Player::renderHeight() {
    return this->windowHeight;
}

int Player::renderWidth() {
    return this->windowWidth;
}

void Player::render(uint8_t *src_data, int scaleWidth, int scaleHeight, int width, int height,
                    int lineSize) {
    pthread_mutex_lock(&windowMutex);
    // LOG_D("----render w = %d ,h = %d", width, height);
    int top = (height - scaleHeight) / 2;
    if (window) {
        ANativeWindow_setBuffersGeometry(window, width, height, WINDOW_FORMAT_RGBA_8888);
        ANativeWindow_Buffer buffer;
        if (ANativeWindow_lock(window, &buffer, nullptr)) {//非0 失败
            ANativeWindow_release(window);
            window = nullptr;
        } else {
            auto dst_data = static_cast<uint8_t *>(buffer.bits);
            int dst_lineSize = buffer.stride * 4;
            for (int i = 0; i < scaleHeight; i++) {
                memcpy(dst_data + (i + top) * dst_lineSize, src_data + i * lineSize, dst_lineSize);
            }
            ANativeWindow_unlockAndPost(window);
        }
    }
    pthread_mutex_unlock(&windowMutex);
}

void Player::setWindow(JNIEnv *env, jobject surface) {
    pthread_mutex_lock(&windowMutex);
    if (window) {
        ANativeWindow_release(window);
        window = nullptr;
    }
    window = ANativeWindow_fromSurface(env, surface);
    this->windowWidth = ANativeWindow_getWidth(window);
    this->windowHeight = ANativeWindow_getHeight(window);
    LOG_D("----window w = %d ,h = %d", windowWidth, windowHeight);
    pthread_mutex_unlock(&windowMutex);
}

void *parseAVPackets(void *st) {
    auto p = static_cast<Player *>(st);
    p->parseDatasource();
    return nullptr;
}

void Player::start() {
    if (state == play)return;
    state = play;
    if (videoChannel)videoChannel->start();
    if (audioChannel)audioChannel->start();
    pthread_create(&parseThread, nullptr, parseAVPackets, this);
}

void Player::parseDatasource() {
    while (state == play) {
        //如果packet缓存过大，先停一下
        if (videoChannel && !videoChannel->canPushPackets()) {
            av_usleep(10 * 1000);
            continue;
        }
        auto packet = av_packet_alloc();

        int res = av_read_frame(avFormatContext, packet);
        if (!res) {
            if (videoChannel && videoChannel->acceptStream(packet->stream_index)) {
                videoChannel->producePacket(packet);
            } else if (audioChannel && audioChannel->acceptStream(packet->stream_index)) {
                audioChannel->producePacket(packet);
            } else {
                Channel::releasePacket(&packet);
            }
        } else if (res == AVERROR_EOF) {//播放完成
            state = over;
            Channel::releasePacket(&packet);
            if (videoChannel->emptyPackets() && audioChannel->emptyPackets())break;
        } else {//未知错误
            state = error;
            Channel::releasePacket(&packet);
            break;
        }
    }
    if (videoChannel)videoChannel->stop();
    if (audioChannel)audioChannel->stop();
}

void Player::stop() {
    if (state == pause)return;
    state = pause;
    if (videoChannel)videoChannel->stop();
    if (audioChannel)audioChannel->stop();
}

//回调再视频播放线程
bool Player::syncAudio(double delay, double videoTime) {
    if (audioChannel) {
        double audioTime = audioChannel->getAudioTime();
        double diff = videoTime - audioTime;
        if (diff > 0) {
            // 视频时间 > 音频时间： 要等音频，所以控制视频播放慢一点（等音频） 【睡眠】
            if (diff > 1) {
                av_usleep((delay * 2) * 1000000);
            } else {   // 说明：0~1之间：音频与视频差距不大，所以可以那（当前帧实际延时时间 + 音视频差值）
                av_usleep((delay + diff) * 1000000); // 单位是微妙：所以 * 1000000
            }
        } else if (diff < 0) {
            if (fabs(diff) <= 0.05) { // fabs对负数的操作（对浮点数取绝对值）
                // 多线程（安全 同步丢包）
                //videoChannel..sync();
                videoChannel->dropFrame();
                return true;
                // 丢完取下一个包
            }
        }
    }
    return false;
}



