package com.morgan.library.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
}
