package com.yingf.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/10/21 11:07 AM
 */
public class DataUtil {
    public synchronized static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public synchronized static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    public synchronized static byte[] getByteArrayByInputStream(byte[] dataArrays, int size, InputStream inputStream) throws IOException {
        int readBytes = 0;
        while (readBytes < size) {
            int read = inputStream.read(dataArrays, readBytes, size - readBytes);
            //判断是不是读到了数据流的末尾 ，防止出现死循环。
            if (read == -1) {
                break;
            }
            readBytes += read;
        }
        return dataArrays;
    }

    /** Java 自带的分割方法无法分割文件分割符, 因此手写一个通用方法 */
    public synchronized static String[] splitStringOnFileSep(String str) {
        char[] chars = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        List<String> res = new ArrayList<>();
        for (char c : chars) {
            if (c != File.separatorChar){
                sb.append(c);
            } else {
                System.out.println(c);
                res.add(sb.toString());
                sb = new StringBuffer();
            }
        }
        res.add(sb.toString());
        String[] result = new String[res.size()];
        res.toArray(result);
        return result;
    }

}
