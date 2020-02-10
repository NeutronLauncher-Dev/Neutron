#include "substrate.h"
#include <jni.h>
//正在施工

//JNI EXPORT

JNIEXPORT jobject JNICALL Java_com_mojang_minecraftpe_MainActivity_JNITools_getJNI(JNIEnv *env, jobject obj) {

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

//MS HOOK

JNIEXPORT jint JNI_OnLoad(JavaVM*,void*){
/*
    void * image = dlopen("libminecraftpe.so",RTLD_LAZY);
    MSHookFunction(dlsym(image,"_ZN9Explosion7explodeEv"),(void*)&onExplode,(void*)&explode);
    MSHookFunction(dlsym(image,"_ZN15MinecraftClient4initEv"),(void*)&onMinecraftClientInit,(void**)&minecraftClientInit);
    MSHookFunction(dlsym(image,"_ZN13MinecraftGame6onTickEv"),(void*)&onTick,(void**)&tick);
    MSHookFunction(dlsym(image,"_ZN18BlockEventListener15onBlockModifiedERK8BlockPosRK5BlockS5_"),(void*)&onBlockModified,(void**)&blockModified);
    MSHookFunction(dlsym(image,"_ZN18BlockEventListener24onBlockDestroyedByPlayerER6PlayerSsRK8BlockPos"),(void*)&onBlockDestroyedByPlayer,(void**)&blockDestroyedByPlayer);

    getGuiData=(void*(*)(void*)) dlsym(image,"_ZN15MinecraftClient10getGuiDataEv");
    displayClientMessage= (void(*)(void*,std::string const&)) dlsym(image,"_ZN7GuiData20displayClientMessageERKSs");
    return JNI_VERSION_1_6;
    */
}