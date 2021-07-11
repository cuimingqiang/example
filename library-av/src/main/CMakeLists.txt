cmake_minimum_required(VERSION 3.4.1)

file(GLOB sources cpp/*.cpp)

add_library(av SHARED ${sources})

find_library(log-lib log)

target_link_libraries(av log)