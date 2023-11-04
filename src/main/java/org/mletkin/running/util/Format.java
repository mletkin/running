package org.mletkin.running.util;

import java.time.Duration;

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

    /**
     * Format meter to km, with 3 decimals
     */
    public static String m2km(double meter) {
        return String.format("%2.3f", meter / 1000);
    }
}
