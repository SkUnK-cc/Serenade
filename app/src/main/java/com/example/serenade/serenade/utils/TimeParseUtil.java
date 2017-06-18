package com.example.serenade.serenade.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Serenade on 17/6/18.
 */

public class TimeParseUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    public static String parse(int duration) {
        String str = sdf.format(new Date(duration));
        return str;
    }
}
