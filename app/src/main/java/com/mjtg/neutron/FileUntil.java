package com.mjtg.neutron;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 *
 * @version 1.0
 * @since 2015-9-11
 * @category com.feng.util
 *
 */
public final class FileUntil
{
    public static void zipUncompress(String inputFile, String destDirPath) throws IOException {
        File srcFile = new File(inputFile);//获取当前压缩文件
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new IOException(srcFile.getPath() + "所指文件不存在");
        }
        //开始解压
        //构建解压输入流
        ZipInputStream zIn = new ZipInputStream(new FileInputStream(srcFile));
        ZipEntry entry = null;
        File file = null;
        while ((entry = zIn.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                file = new File(destDirPath, entry.getName());
                if (!file.exists()) {
                    new File(file.getParent()).mkdirs();//创建此文件的上级目录
                }
                OutputStream out = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(out);
                int len = -1;
                byte[] buf = new byte[1024];
                while ((len = zIn.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                // 关流顺序，先打开的后关闭
                bos.close();
                out.close();
            }
        }
    }
    public static String readAssets(Context context, String fileName){
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String readFile(String path){
        StringBuilder res=new StringBuilder();
        try {
            FileReader reader = new FileReader(new File(path));
            BufferedReader bufferedReader=new BufferedReader(reader);
            String line;
            while((line=bufferedReader.readLine())!=null){
                res.append(line);
            }
            bufferedReader.close();
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return res.toString();
    }
    public static boolean deletefile(String path){
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return false;
        }
        String[] str = file.list();
        for (int i = 0; i < str.length; i++) {
            System.out.println("333:"+str[i]);
            File fi = new File(path + "/" + str[i]);
            if (path.endsWith(File.separator)) {
                fi = new File(path + str[i]);
            } else {
                fi = new File(path + File.separator + str[i]);
            }

            if(fi.exists()||fi.list().length==0){
                File myFilePath = new File(path+"/"+str[i]);
                myFilePath.delete();
            }
            if(fi.isDirectory())//如果文件假内还有 就继续调用本方法
            {
                deletefile(path+"/"+str[i]);
            }else{
                fi.delete();
            }

        }
        return true;
    }


}
