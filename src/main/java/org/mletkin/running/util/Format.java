package org.mletkin.running.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Format {

    /**
     * Format pace mm:ss
     */
    public static String pace(Duration pace) {
        long min = pace.getSeconds() / 60;
        long sec = pace.getSeconds() % 60;

        return String.format("%02d:%02d", min, sec);
    }

    /**
     * Format duration as hh:mm:ss
     */
    public static String time(Duration pace) {
        long hrs = pace.getSeconds() / 3600;
        long min = (pace.getSeconds() / 60) % 60;
        long sec = pace.getSeconds() % 60;

        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

    public static String date(LocalDate date) {
        return date == null ? "--" : date.toString();
    }

    public static String date(LocalDateTime dateTime) {
        return date(dateTime.toLocalDate());
    }

    public static String time(LocalDateTime dateTime) {
        return dateTime == null ? "--" : dateTime.toLocalTime().toString();
    }

    public static String percentage(double value) {
        return ((int) 100 * value + 0.5) + " %";
    }

}
