package com.yingf.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;


public abstract class FileUtil {

    public static final int ILLEGAL_NUM = 31;

    /**
     * Java文件操作 获取文件扩展名
     *
     * @param filename 文件名
     */
    public static String getExtensionName(String filename) {
        if (filename == null) {
            return "";
        } else {
            if ((filename.length() > 0)) {
                int dot = filename.lastIndexOf('.');
                if ((dot > -1) && (dot < (filename.length() - 1))) {
                    return filename.substring(dot + 1);
                }
            }
            return filename.toLowerCase();
        }
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     *
     * @param filename 文件名
     */
    public static String getFileNameWithOutSuffix(String filename) {
        if (filename == null) {
            return "";
        } else {
            if ((filename.length() > 0)) {
                int dot = filename.lastIndexOf('.');
                if ((dot > -1) && (dot < (filename.length()))) {
                    return filename.substring(0, dot);
                }
            }
            return filename.toLowerCase();
        }
    }

    /**
     * 根据File对象获取MD5值
     * @param file File 对象
     * @return File对象的MD5
     */
    public static String getFileMd5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        int buffSize = 1024;
        int len;
        byte[] buffer = new byte[buffSize];
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, buffSize)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String s = bigInt.toString(16);
        if (s.length() == ILLEGAL_NUM) {
            s = "0" + s;
        }
        return s;
    }
}
