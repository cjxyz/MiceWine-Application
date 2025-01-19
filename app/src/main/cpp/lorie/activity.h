#ifndef ACTIVITY_H
#define ACTIVITY_H

extern const char *packageNameGlobal;

void setPackageName(const char *packageName);

JNIEXPORT void JNICALL Java_com_micewine_emu_MainActivity_nativeSetPackageName(JNIEnv *env, jobject obj, jstring packageName);

#endif // ACTIVITY_H