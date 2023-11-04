package org.mletkin.running.util;

import java.time.LocalDateTime;

import javax.xml.datatype.XMLGregorianCalendar;

public class Util {

    /**
     * Distance between two lat/long points.
     *
     * @see https://en.wikipedia.org/wiki/Haversine_formula
     * @see https://www.kompf.de/gps/distcalc.html
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = lat1 * Math.PI / 180;
        lon1 = lon1 * Math.PI / 180;
        lat2 = lat2 * Math.PI / 180;
        lon2 = lon2 * Math.PI / 180;

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.pow(Math.sin(dLat / 2.0), 2) + //
                Math.pow(Math.sin(dLon / 2.0), 2) * Math.cos(lat1) * Math.cos(lat2);
        double dist = 6378.388 * 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return dist;
    }

    public static LocalDateTime toLocalDateTime(XMLGregorianCalendar xgc) {
        return xgc == null //
                ? null
                : xgc.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }

}
