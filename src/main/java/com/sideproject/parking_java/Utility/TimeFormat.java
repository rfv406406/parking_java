package com.sideproject.parking_java.Utility;

import java.text.SimpleDateFormat;


public class TimeFormat {
    public static String timeFormat(Object timeObj) {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        return ft.format(timeObj);
    }
}
