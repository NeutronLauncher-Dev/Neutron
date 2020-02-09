#include "substrate.h"
#include <jni.h>
#include <string>



JNIEXPORT jint JNI_OnLoad(JavaVM*,void*)
{
    void * image = dlopen("libminecraftpe.so",RTLD_LAZY);
    return JNI_VERSION_1_6;
}