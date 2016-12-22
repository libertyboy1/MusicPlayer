package com.view.media.utils;

/**
 * Created by Destiny on 2016/12/20.
 */

public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0 || "null".equals(str) || "".equals(str);
    }

}
