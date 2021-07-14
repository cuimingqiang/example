cmake_minimum_required(VERSION 3.4.1)

set(FFmpeg ${CMAKE_SOURCE_DIR}/cpp/ffmpeg)
include_directories(${FFmpeg}/include)
string(REGEX REPLACE "(.*)/(.*)/(.*)" "\\1" PROJECT_INIT_PATH  ${PROJECT_SOURCE_DIR})
set(FFmpegSo ${PROJECT_INIT_PATH}/libs)
message("上层目录=" ${PROJECT_INIT_PATH})
message("libs=" ${FFmpegSo})
add_library(avcodec SHARED IMPORTED)
add_library(avfilter SHARED IMPORTED)
add_library(avformat SHARED IMPORTED)
add_library(avutil SHARED IMPORTED)
add_library(swresample SHARED IMPORTED)
add_library(swscale SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION
        ${FFmpegSo}/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.so)
set_target_properties(avfilter PROPERTIES IMPORTED_LOCATION
        ${FFmpegSo}/${CMAKE_ANDROID_ARCH_ABI}/libavfilter.so)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION
        ${FFmpegSo}/${CMAKE_ANDROID_ARCH_ABI}/libavformat.so)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION
        ${FFmpegSo}/${CMAKE_ANDROID_ARCH_ABI}/libavutil.so)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION
        ${FFmpegSo}/${CMAKE_ANDROID_ARCH_ABI}/libswresample.so)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION
        ${FFmpegSo}/${CMAKE_ANDROID_ARCH_ABI}/libswscale.so)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${FFmpegSo}/${CMAKE_ANDROID_ARCH_ABI}") #set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
find_library(log-lib log)
file(GLOB sources cpp/*.cpp)
add_library(av SHARED ${sources})
target_link_libraries(av
        log
        -Wl,--start-group
        avcodec avfilter avformat avutil swresample swscale
        -Wl,--end-group
        z
        android
        OpenSLES
        )