cmake_minimum_required(VERSION 3.6.4111459)

set(FFMPEG ${CMAKE_SOURCE_DIR}/ffmpeg) # ffmpeg的路径
set(RTMP ${CMAKE_SOURCE_DIR}/rtmp) # rtmp的路径

include_directories(${FFMPEG}/include) # 导入ffmpeg的头文件

set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -L${FFMPEG}/libs/${CMAKE_ANDROID_ARCH_ABI}") # ffmpeg库指定

set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -L${RTMP}/libs/${CMAKE_ANDROID_ARCH_ABI}") # rtmp库指定

# 批量导入 源文件
file(GLOB src_files *.cpp)

add_library(
        player # 总库libnative-lib.so
        SHARED # 动态库
        ${src_files})

target_link_libraries(
        player # 总库libnative-lib.so

        # 忽略顺序的方式，导入
        -Wl,--start-group
        avcodec avfilter avformat avutil swresample swscale
        -Wl,--end-group

        log # 日志库，打印日志用的
        z # libz.so库，是FFmpeg需要用ndk的z库，FFMpeg需要额外支持  libz.so
        rtmp #
        android
        OpenSLES
)