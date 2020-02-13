#include "substrate.h"
#include <string.h>
#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <assert.h>

#define  LOG_TAG    "native-dev"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

//正在施工

//algorithm

char* JstringToChar(JNIEnv* env, jstring jstr) {
	if(jstr == NULL) {
		return NULL;
	}
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);
	return rtn;
}


//METHODS INSTANCE

/*
void (*displayClientMessage)(void*,std::string const&);
void* (*getGuiData)(void*);

void (*minecraftClientInit)(void*);
void onMinecraftClientInit(void*thiz){
    //todo
    minecraftClientInit(thiz);
}
void (*tick)(void*);
void onTick(void*thiz){
    //todo
    tick(thiz);
}
void (*explode)(void*thiz);
void onExplode(void*thiz){
    //todo
}
void (*blockModified)(void const& block1, void const& block2, void const& block3);
void onBlockModified(void const&, void const&, void const&){
    //todo
}
void (*blockDestroyedByPlayer)(void & , std::string, void const&);
void onBlockDestroyedByPlayer(void*){
    //todo
}
*/

void putNativeLog(jstring str){
    //__android_log_print(ANDROID_LOG_INFO, "native", JstringToChar(env, str));
}

//MS HOOK & D

static JNINativeMethod getMethods[] = {
        {"putNativeLog","(Ljava/lang/String;)V",(void*)putNativeLog},
};

static int registerNativeMethods(JNIEnv* env, const char* className,JNINativeMethod* getMethods,int methodsNum){
    jclass clazz;
    clazz = env->FindClass(className);
    if(clazz == NULL){
        return JNI_FALSE;
    }
    if(env->RegisterNatives(clazz,getMethods,methodsNum) < 0){
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv* env){
    const char* className  = "com/mjtg/neutron/runtime/NeutronHooks";
    return registerNativeMethods(env,className,getMethods, sizeof(getMethods)/ sizeof(getMethods[0]));
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){

    /*
        void * image = dlopen("libminecraftpe.so",RTLD_LAZY);
        MSHookFunction(dlsym(image,"_ZN9Explosion7explodeEv"),(void*)&onExplode,(void*)&explode);
        MSHookFunction(dlsym(image,"_ZN15MinecraftClient4initEv"),(void*)&onMinecraftClientInit,(void**)&minecraftClientInit);
        MSHookFunction(dlsym(image,"_ZN13MinecraftGame6onTickEv"),(void*)&onTick,(void**)&tick);
        MSHookFunction(dlsym(image,"_ZN18BlockEventListener15onBlockModifiedERK8BlockPosRK5BlockS5_"),(void*)&onBlockModified,(void**)&blockModified);
        MSHookFunction(dlsym(image,"_ZN18BlockEventListener24onBlockDestroyedByPlayerER6PlayerSsRK8BlockPos"),(void*)&onBlockDestroyedByPlayer,(void**)&blockDestroyedByPlayer);

        getGuiData=(void*(*)(void*)) dlsym(image,"_ZN15MinecraftClient10getGuiDataEv");
        displayClientMessage= (void(*)(void*,std::string const&)) dlsym(image,"_ZN7GuiData20displayClientMessageERKSs");
    */

    JNIEnv* env = NULL;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    if(!registerNatives(env)){
        return -1;
    }
    return JNI_VERSION_1_6;
}
