package com.sideproject.parking_java.utility;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class TimeUtils {
    public static String timeFormat(Object timeObj) {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        ft.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        return ft.format(timeObj);
    }

    public static int timeCalculate(String timeObj1, String timeObj2) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime  timeObj1ParsedDate = LocalDateTime.parse(timeObj1, formatter);
        LocalDateTime  timeObj2ParsedDate = LocalDateTime.parse(timeObj2, formatter);

        Duration duration = Duration.between(timeObj1ParsedDate, timeObj2ParsedDate);

        int usingTime = (int) duration.toSeconds();

        return usingTime;
    }
    
}
