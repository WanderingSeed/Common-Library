package com.morgan.library.utils;

import java.io.File;
import java.util.Comparator;

/**
 * 文件比较器，比较最后修改时间
 * 
 * @author JiGuoChao
 * 
 * @version 1.0
 * 
 * @date 2015-7-30
 */
public class FileModifiedComparator implements Comparator<File> {

	@Override
	public int compare(File file1, File file2) {
		long last1 = file1.lastModified();
		long last2 = file2.lastModified();
		if (last1 == last2) {
			return 0;
		} else if (last1 < last2) {
			return -1;
		} else {
			return 1;
		}
	}

}