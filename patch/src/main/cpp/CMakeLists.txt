cmake_minimum_required(VERSION 3.6)



find_library( log-lib log )

add_library( substrate SHARED IMPORTED )
set_target_properties(
        substrate
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libsubstrate.so
)

add_library( substrateDVM SHARED IMPORTED )
set_target_properties(
        substrateDVM
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libsubstrate-dvm.so
)


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
    substrate
    substrateDVM
)