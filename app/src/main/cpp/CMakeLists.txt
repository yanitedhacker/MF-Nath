cmake_minimum_required(VERSION 3.22.1)
project("doomsy")

# llama.cpp will be added in Task 5
add_library(doomsy SHARED placeholder.cpp)

target_link_libraries(doomsy android log)
