cmake_minimum_required(VERSION 3.4.1)

set(FFmpeg ${CMAKE_SOURCE_DIR}/cpp/ffmpeg)

include_directories(${FFmpeg}/include)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${FFmpeg}/libs/${CMAKE_ANDROID_ARCH_ABI}")

file(GLOB sources cpp/*.cpp)

add_library(av SHARED ${sources})

find_library(log-lib log)

target_link_libraries(av
        log
        -Wl,--start-group
        avcodec avfilter avformat avutil swresample swscale
        -Wl,--end-group
        z
        android
        OpenSLES
        )