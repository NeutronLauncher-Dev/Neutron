//
// Created by Administrator on 2020/2/8.
//

#include "substrate.h"
#include <dlfcn.h>
#include <android/log.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "HookGetSpeed", __VA_ARGS__)
#define LIB "/data/app-lib/com.shandagames.bop-1/libShellClient.so"

MSConfig(MSFilterExecutable,"/system/bin/app_process")
//MSConfig(MSFilterLibrary, "libdvm.so");

int (* old_getSpeed)(void);
int  new_getSpeed(void * CCharacter){
    LOGD("getSpeed %d",(&CCharacter + 28));
    return 1;
}


int (* old_SpeedUp)(int);
int new_SpeedUp(int a2){
    LOGD("a2 : %d",a2);
    return old_SpeedUp(a2);
}

int (* old_SetScale)(int,float);
int new_SetScale(int result,float a2){
    LOGD("result : %d",result);
    LOGD("a2 : %d",a2);
    *(float *)(result + 1136) = a2;
    return result;
}


//通过so库的绝对路径和函数名，找到其函数的映射地址
void* lookup_symbol(char* libraryname,char* symbolname)
{
    //获取so库的句柄
    void *handle = dlopen(libraryname, RTLD_GLOBAL | RTLD_NOW);
    if (handle != NULL){
        LOGD("dlopen success");
        void * symbol = dlsym(handle, symbolname);
        if (symbol != NULL){

            LOGD("dlsym %s success",symbolname);
            return symbol;
        }else{
            LOGD("dl error: %s", dlerror());
            return NULL;
        }
    }else{
        LOGD("dlopen %s faile",libraryname);
        return NULL;
    }
}

MSInitialize
{
    //获取hook函数的地址,最好不要用下面MS提供的方法
    void * symbol = lookup_symbol(LIB,"_ZN16CGameSceneClient8SetScaleEf");
    if(symbol != NULL){
        LOGD("symbol address is 0x%08X",&symbol);
        //这里将旧函数的入口（参数一）指向hello(参数三），然后执行新函数（参数二）
        MSHookFunction(symbol, (void *)&new_SetScale, (void **)&old_SetScale);
        LOGD("MSHookFunction finish");
    }
}