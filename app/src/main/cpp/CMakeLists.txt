cmake_minimum_required(VERSION 3.22.1)
project("doomsy")

set(LLAMA_DIR ${CMAKE_CURRENT_SOURCE_DIR}/llama.cpp)

set(BUILD_SHARED_LIBS OFF)
set(GGML_OPENMP OFF)
set(GGML_LLAMAFILE OFF)

add_subdirectory(${LLAMA_DIR} llama.cpp)

add_library(doomsy SHARED llama_jni.cpp)

target_include_directories(doomsy PRIVATE
    ${LLAMA_DIR}/include
    ${LLAMA_DIR}/ggml/include
)

target_link_libraries(doomsy
    llama
    ggml
    common
    android
    log
)
