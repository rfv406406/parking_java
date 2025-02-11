package com.sideproject.parking_java.Model;

import java.text.SimpleDateFormat;


public class TimeModel {
    public static String timeFormat(Object timeObj) {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        return ft.format(timeObj);
    }
}
