# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)


project("lab1project")

add_library(
        lab1project
        SHARED
        native-lib.cpp)

add_library(spdlog STATIC IMPORTED)
set_target_properties(spdlog PROPERTIES IMPORTED_LOCATION
        C:/Users/user/AndroidStudioProjects/libs/spdlog/build/x86_64/libspdlog.a)

add_library(mbedcrypto SHARED IMPORTED)
set_target_properties(mbedcrypto PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../nativeLibs/${ANDROID_ABI}/libmbedcrypto.so)

target_include_directories(lab1project PUBLIC
        C:/Users/user/AndroidStudioProjects/libs/spdlog/spdlog/include
        C:/Users/user/AndroidStudioProjects/libs/mbedtls//mbedtls/include
        )
find_library(
        log-lib
        log)

target_link_libraries(
        lab1project
        spdlog
        mbedcrypto
        ${log-lib})