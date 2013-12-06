package com.morgan.library.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class SDCardUtils {

    public static String getSDCardPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static long getSDCardAvailableBytes()
    {
        if (isSDCardBusy()) return 0;
        final File path = Environment.getExternalStorageDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long blockSize = stat.getBlockSize();
        final long availableBlocks = stat.getAvailableBlocks();
        return blockSize * (availableBlocks - 4);
    }

    public static boolean isSDCardBusy()
    {
        return !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
