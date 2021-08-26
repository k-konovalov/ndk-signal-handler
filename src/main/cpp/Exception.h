#ifndef PRESENTATION_EXCEPTION_H
#define PRESENTATION_EXCEPTION_H

#include <jni.h>

void pendingException(JNIEnv *env, jclass thiz);
void pendingExceptionPositive(JNIEnv *env, jclass thiz);
void throwException(JNIEnv *env, jclass thiz);
void crashHandler(JNIEnv *env, jclass thiz);

static void registerSignalHandlerNew();


#endif //PRESENTATION_EXCEPTION_H
