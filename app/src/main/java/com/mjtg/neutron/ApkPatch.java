package com.mjtg.neutron;

import java.io.File;
public class ApkPatch {
    public static void start(String path){
        final String path_=path;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sync(path_);
            }
        }).start();
    }
    private static void sync(String path){
        File file=new File(path);
        String str=file.getParent()+File.pathSeparator+"temp";
        ZipUtil.unzip(file.getPath(),file.getParent());
        file.delete();
        String dexPath=file.getParent()+File.pathSeparator+"classes.dex";

        ZipUtil.zip(file.getParentFile().list(),file.getParent()+File.pathSeparator+"temp.apk");
    }
}
