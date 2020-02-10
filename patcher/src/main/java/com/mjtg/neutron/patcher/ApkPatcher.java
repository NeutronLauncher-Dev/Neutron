package com.mjtg.neutron.patcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.zip.ZipOutputStream;

public class ApkPatcher {

    public static void start(final String path,final String dexPath,final String substratePath,final String insertPath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sync(path,dexPath,substratePath,insertPath);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void copy(String from,String to) throws IOException{
        FileChannel inputChannel=null;
        FileChannel outputChannel=null;
        new File(to).createNewFile();
        try{
            inputChannel=new FileInputStream(new File(from)).getChannel();
            outputChannel=new FileOutputStream(to).getChannel();
            outputChannel.transferFrom(inputChannel,0,inputChannel.size());
        } finally {
            if (outputChannel!=null) {
                inputChannel.close();
            }
            if (outputChannel!=null) {
                outputChannel.close();
            }
        }
    }

    private static void sync(String path,String dexPath,String substratePath,String insertPath) throws IOException {
        String s=File.separator;
        File file=new File(path);

        //unpack
        System.out.println("Unpacking apk...");
        ZipUtil.unzip(file.getPath(),file.getParent()+s+"temp"+s);
        String libPath=file.getParent()+String.format("%stemp%slib%sarmeabi-v7a",s,s,s);
        File dexFile=new File(file.getParent()+String.format("%stemp%sclasses.dex",s,s));

        //inject dex
        System.out.println("Inject dex...");
        dexFile.delete();
        copy(dexPath,dexFile.getPath());

        //inject libs
        System.out.println("Inject libs...");
        copy(substratePath,libPath+s+"libsubstrate.so");
        copy(insertPath,libPath+s+"libneutron.so");

        //packfile
        System.out.println("Packing libs...");
        file.delete();
        File f=new File(file.getPath()+".apk");
        f.createNewFile();
        ZipOutputStream zos=new ZipOutputStream(new FileOutputStream(f));
        ZipUtil.zipFile(new File(file.getParent()+s+"temp"),"",zos);
        zos.close();
        System.out.println("Patched successfully");
    }

}