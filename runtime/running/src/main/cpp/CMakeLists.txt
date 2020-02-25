cmake_minimum_required(VERSION 3.6)

find_library( log-lib log )


add_library(
    neutron
    SHARED
        dummy.cpp
        neutron.cy.cpp
)

target_link_libraries(
    neutron
    PRIVATE
    ${log-lib}
    ${CMAKE_SOURCE_DIR}/libs/libsubstrate.so
)