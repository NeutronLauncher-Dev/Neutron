cmake_minimum_required(VERSION 3.6)

find_library( log-lib log )


add_library(
    stub
    SHARED
        stub.cpp
)

target_link_libraries(
    stub
    PRIVATE
    ${log-lib}
)