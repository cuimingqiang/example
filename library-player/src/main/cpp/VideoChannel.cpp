//
// Created by cuimingqiang on 2021/7/14.
//

#include "VideoChannel.h"

VideoChannel::VideoChannel(int streamIndex, AVCodecContext *context, AVRational base,
                           ChannelType type,
                           JavaPlayerHolder *holder, int fps) : Channel(streamIndex, context, base,
                                                                        type,
                                                                        holder), fps(fps),
                                                                renderHeight(0), renderWidth(0) {

}

VideoChannel::~VideoChannel() {
    render = nullptr;
}

void VideoChannel::setRender(RenderVideo *render) {
    this->render = render;
}

void VideoChannel::play() {
    AVFrame *frame = nullptr;
    uint8_t *rgba[4];
    int rgbaLine[4];
    av_image_alloc(rgba, rgbaLine, avCodecContext->width, avCodecContext->height, AV_PIX_FMT_RGBA,
                   1);
    int width = avCodecContext->width;
    int height = avCodecContext->height;
    LOG_D("----video w = %d ,h = %d", width, height);
    this->renderHeight = render->renderHeight();
    this->renderWidth = render->renderWidth();
    SwsContext *sws_ctx = nullptr;

    width = renderWidth;
    height = renderHeight;
    LOG_D("----video compute w = %d ,h = %d", width, height);
    int scaleHeight = avCodecContext->height * width /
                      avCodecContext->width;//height * avCodecContext->height / renderHeight;
    LOG_D("----video w = %d ,h = %d, sh = %d", width, height, scaleHeight);
    sws_ctx = sws_getContext(avCodecContext->width, avCodecContext->height,
                             avCodecContext->pix_fmt,
                             width, scaleHeight,
                             AV_PIX_FMT_RGBA,
                             SWS_BILINEAR, NULL, NULL, NULL);
    while (isPlay) {
        int ret = frames.consume(frame);
        if (!isPlay)break;
        if (ret)continue;//非0 标识失败
        sws_scale(sws_ctx, frame->data, frame->linesize, 0, avCodecContext->height, rgba, rgbaLine);
        double extra_delay = frame->repeat_pict / (2.0 * fps);
        double fps_delay = 1.0 / fps;
        double real_delay = fps_delay + extra_delay;
        double video_time = frame->best_effort_timestamp * av_q2d(timeBase);
        av_frame_unref(frame);
        releaseFrame(&frame);
        if(render->syncAudio(real_delay, video_time))continue;
        render->render(rgba[0], width, scaleHeight, width, height, rgbaLine[0]);

    }
    av_frame_unref(frame); // 减1 = 0 释放成员指向的堆区
    releaseFrame(&frame); // 释放AVFrame * 本身的堆区空间
    isPlay = false;
    av_free(&rgba[0]);
    sws_freeContext(sws_ctx);
}

void VideoChannel::dropFrame() {
    frames.dropFirst();
}
