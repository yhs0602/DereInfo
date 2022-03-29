
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)
set(CMAKE_VERBOSE_MAKEFILE ON)
# Not your situation, but in case you have a different linker
# set(CMAKE_EXE_LINKER_FLAGS "-Wl,--verbose")

# Clang passes flags through to the linker (likely ld) by
# set(CMAKE_EXE_LINKER_FLAGS "-Xlinker -v")

# Clang passing flags to the linker (likely ld) AND using -v itself to show how it calls the linker
set(CMAKE_EXE_LINKER_FLAGS "-Xlinker -v -v")


add_compile_definitions(__CGSS_WITH_JNI__=ON)


# Declares and names the project.

project("dereinfo")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

FILE(GLOB SRCFILES1 "lib/capi/*.cpp")
FILE(GLOB SRCFILES2 "lib/cdata/*.cpp")
FILE(GLOB SRCFILES3 "lib/cenum/*.cpp")
FILE(GLOB SRCFILES4 "lib/common/*.cpp")
FILE(GLOB SRCFILES5 "lib/ichinose/*.cpp")
FILE(GLOB SRCFILES6 "lib/jni/*.cpp")
FILE(GLOB SRCFILES7 "lib/jni/helpers/*.cpp")
FILE(GLOB SRCFILES8 "lib/jni/modules/*.cpp")
FILE(GLOB SRCFILES9 "lib/jni/modules/kawashima/*.cpp")
FILE(GLOB SRCFILES10 "lib/jni/modules/takamori/*.cpp")
FILE(GLOB SRCFILES11 "lib/kawashima/*.cpp")
FILE(GLOB SRCFILES12 "lib/kawashima/hca/*.cpp")
FILE(GLOB SRCFILES13 "lib/kawashima/hca/internal/*.cpp")
FILE(GLOB SRCFILES14 "lib/kawashima/wave/*.cpp")
FILE(GLOB SRCFILES15 "lib/takamori/*.cpp")
FILE(GLOB SRCFILES16 "lib/takamori/exceptions/*.cpp")
FILE(GLOB SRCFILES17 "lib/takamori/streams/*.cpp")
FILE(GLOB SRCFILES18 "common/*.cpp")
FILE(GLOB SRCFILES19 "common/*.c")
FILE(GLOB SRCFILES20 "common/acbextract/*.cpp")


add_library( # Sets the name of the library.
             dereinfo

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             dereinfo.cpp

             ${SRCFILES1}

             ${SRCFILES2}
             ${SRCFILES3}
             ${SRCFILES4}
             ${SRCFILES5}
             ${SRCFILES6}
             ${SRCFILES7}
             ${SRCFILES8}
             ${SRCFILES9}
             ${SRCFILES10}
             ${SRCFILES11}
             ${SRCFILES12}
             ${SRCFILES13}
             ${SRCFILES14}
             ${SRCFILES15}
             ${SRCFILES16}
             ${SRCFILES17}
             ${SRCFILES18}
             ${SRCFILES19}
             ${SRCFILES20}
             )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       dereinfo

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
