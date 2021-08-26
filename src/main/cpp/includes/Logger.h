#ifndef PRESENTATION_LOGGER_H
#define PRESENTATION_LOGGER_H

#include <android/log.h>

#define APPNAME "NDK_SIGNAL_HANDLER"

#define DEMO_LOG(...) __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, __VA_ARGS__);
#define CHECK_ERROR() DEMO_LOG("Error\nerrno: %i\ndescription: %s", errno, strerror(errno))

#endif //PRESENTATION_LOGGER_H
