package com.view.media.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Destiny on 2016/12/19.
 */

public class TimeUtil {
    private static SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");

    public static String getStrTimeFromeTimestamp(Long timestamp){
        return sdf.format(new Date(timestamp));
    }
}
