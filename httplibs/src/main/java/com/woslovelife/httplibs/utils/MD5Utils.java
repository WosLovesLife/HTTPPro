package com.woslovelife.httplibs.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangh on 2016/11/6.
 */

public class MD5Utils {
    public static String encoding(String source) {
        if (null == source || source.equals("")) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(source.getBytes("utf-8"));
            byte[] bytes = digest.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                String hexString = Integer.toHexString(aByte & 0xFF);
                if (hexString.length()==1){
                    stringBuilder.append("0");
                }
                stringBuilder.append(hexString);
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}