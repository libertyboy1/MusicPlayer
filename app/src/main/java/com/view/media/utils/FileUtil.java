package com.view.media.utils;

import android.os.Environment;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Destiny on 2016/12/19.
 */

public class FileUtil {

    public static File[] getFiles(String path) {
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        return file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.getName().endsWith(".mp3")||file.getName().endsWith(".m4a")||file.getName().endsWith(".flac")
                        ||file.getName().endsWith(".ape")||file.getName().endsWith(".wav")||file.getName().endsWith(".wma")){
                    return true;
                }
                return false;
            }
        });
    }

    public static void createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean isExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 写入内容到SD卡中的txt文本中
     * str为内容
     */
    public static void writeSDFile(String str, String path, String fileName) throws IOException {
            File file = new File(path, fileName);

            if (!file.exists()) {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(str.getBytes());
                fos.close();
                System.out.println("写入成功：");
            }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

}
