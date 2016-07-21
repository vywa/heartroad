package com.hykj.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by zhaoyu on 16-5-18.
 */
public class Decoder {
    public static String getEncoder(String s) {
        String string = null;
        try {
            string = URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    public static String getDecoder(String s) {
        String string = null;
        try {
            string = URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }
}
