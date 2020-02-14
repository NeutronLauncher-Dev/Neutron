#include "substrate.h"
#include <string.h>
#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <assert.h>
#include <string>
#include <cstdint>

#define  LOG_TAG    "native-dev"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

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

void* mcClient;
JNIEnv* env = NULL;
jmethodID cid;
jclass cls;
jobject instance;
void (*displayClientMessage)(void*,std::string const&);

void* (*getGuiData)(void*);

void (*minecraftClientInit)(void*);
void onMinecraftClientInit(void*thiz){
    //todo
    minecraftClientInit(mcClient=thiz);
}
void (*useItem)(void* thisPtr, void* itemStackPtr, int32_t itemUseMethod, int8_t boo);
void onUseItem(void* thisPtr, void* itemStackPtr, int32_t itemUseMethod, bool boo){
    LOGI("intercepted onUseItem!");
    (env)->CallVoidMethod(instance,cid,itemStackPtr, itemUseMethod,boo);
    useItem(thisPtr, itemStackPtr, itemUseMethod, boo);
}


void displayClientMessage_(JNIEnv *env, jclass clazz, jstring str){
    jboolean isCopy = false;
    const jchar* ch = env->GetStringChars(str, &isCopy);
    std::string str2((const char*)ch);
    displayClientMessage(getGuiData(mcClient), str2);
    env->ReleaseStringChars(str, ch);
}
/*
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

//MS HOOK & D

static JNINativeMethod getMethods[] = {
        {"displayClientMessage","(Ljava/lang/String;)V",(void*)displayClientMessage_}
};


static int registerNativeMethods(JNIEnv* env, const char* className, JNINativeMethod* getMethods, int methodsNum){
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
    const char* className  = "com/mjtg/neutron/api/hooks/NeutronHooks";
    return registerNativeMethods(env,className,getMethods, sizeof(getMethods)/ sizeof(getMethods[0]));
}

extern "C" JNIEXPORT jint JNICALL Java_com_mjtg_neutron_hook_NeutronHooking_performHooking(JNIEnv* env, jclass clazz, jlong addr) {

    __android_log_print(ANDROID_LOG_WARN, "neutron-test", "loading neutron .so!\n");
    assert(env != NULL);
    LOGI("registering natives...");
    if(!registerNatives(env)){
        return -1;
    }

    LOGI("registered!");
    LOGI("registering hook JNI!");
    cls = env->FindClass("com/mjtg/neutron/api/hooks/NeutronHooks");
    jfieldID id = env->GetStaticFieldID(cls, "INSTANCE", "Lcom/mjtg/neutron/api/hooks/NeutronHooks;");
    instance = env->GetStaticObjectField(cls, id);
    cid = env->GetMethodID(cls,"onUseItem", "(JJZ)V");

    LOGI("hooking!");
    void * image = (void*)addr;
    LOGI("minecraft image: %ld", (long)image);
    LOGI("onUseItemHook: %ld", (long)dlsym(image,"_ZN6Player7useItemER9ItemStack13ItemUseMethodb"));
    MSHookFunction(dlsym(image,"_ZN15MinecraftClient4initEv"),(void*)&onMinecraftClientInit,(void**)&minecraftClientInit);
    MSHookFunction(dlsym(image,"_ZN6Player7useItemER9ItemStack13ItemUseMethodb"),(void*)onUseItem,(void**)&useItem);
    LOGI("old use item: %ld", (long)useItem);

    /*
    MSHookFunction(dlsym(image,"_ZN13MinecraftGame6onTickEv"),(void*)&onTick,(void**)&tick);
    MSHookFunction(dlsym(image,"_ZN18BlockEventListener15onBlockModifiedERK8BlockPosRK5BlockS5_"),(void*)&onBlockModified,(void**)&blockModified);
    MSHookFunction(dlsym(image,"_ZN18BlockEventListener24onBlockDestroyedByPlayerER6PlayerSsRK8BlockPos"),(void*)&onBlockDestroyedByPlayer,(void**)&blockDestroyedByPlayer);
    MSHookFunction(dlsym(image,"_ZN9Explosion7explodeEv"),(void*)&onExplode,(void*)&explode);
    */
    getGuiData=(void*(*)(void*)) dlsym(image,"_ZN15MinecraftClient10getGuiDataEv");
    displayClientMessage= (void(*)(void*,std::string const&)) dlsym(image,"_ZN7GuiData20displayClientMessageERKSs");

    return 0;
}