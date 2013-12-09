package com.morgan.library.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.graphics.Bitmap;

public class FileUtils {
    private static final String TAG = FileUtils.class.getName();

    /**
     * 追加内容到文件中
     * 
     * @param filePath 文件的路径
     * @param content 想要写入的信息
     */
    public static void writeFile(String filePath, String content)
    {
        writeFile(filePath, content, true);
    }

    /**
     * @param filePath 文件的路径
     * @param content 想要写入的信息
     * @param append 添加方式(true为追加,false为覆盖)
     */
    public static void writeFile(String filePath, String content, boolean append)
    {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file, append);
            pw = new PrintWriter(fw);
            pw.write(content + "\r\n");
            pw.close();
            fw.close();
        } catch (Exception e) {
            Logger.e(TAG, "写文件发生错误", e);
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    Logger.e(TAG, "写文件发生错误", e);
                }
            }
        }
    }

    /**
     * 用于读取较小文本内的数据
     * 
     * @param inputStream
     * @return
     */
    public static String readFile(String filePath)
    {
        BufferedInputStream buffered = null;
        String content = "";
        try {
            File file = new File(filePath);
            if (file.exists()) { return ""; }
            buffered = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[buffered.available()];
            buffered.read(buffer);
            content = new String(buffer);
        } catch (Exception e) {
            Logger.e(TAG, "读文件发生错误", e);
        } finally {
            if (buffered != null) {
                try {
                    buffered.close();
                } catch (IOException e) {
                    Logger.e(TAG, "读文件发生错误", e);
                }
            }
        }
        return content;
    }

    public static boolean storeImage(Bitmap bitmap, String filePath)
    {
        boolean result = false;
        FileOutputStream b = null;
        try {
            b = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, b);
            result = true;
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                Logger.e(TAG, e.getMessage());
            }
        }
        return result;
    }
    
    public static void copyFile(File sourceFile, File targetFile) throws IOException
    {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null) inBuff.close();
            if (outBuff != null) outBuff.close();
        }
    }
}
