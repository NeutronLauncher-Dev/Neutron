#include<jni.h>
#include "dlfcn.h"

extern "C" JNIEXPORT jlong JNICALL Java_com_mjtg_neutron_runtime_hook_PESORegistry_getPEAddress(JNIEnv* env, jclass clazz) {
    return (jlong)dlopen("libminecraftpe.so",RTLD_LAZY);
}