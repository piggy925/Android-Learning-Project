package com.mattcao.androidlearningproject.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String formatDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("E, MM dd, yyyy");
        return df.format(date);
    }
}
