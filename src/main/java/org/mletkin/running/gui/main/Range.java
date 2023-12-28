package org.mletkin.running.gui.main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.ChronoField;

import org.mletkin.running.model.Session;
import org.mletkin.running.util.Format;

/**
 * Represents an interval of days with a describing Text.
 */
public record Range(LocalDate start, LocalDate end, String txt) {

    /**
     * Create a range for a single day.
     */
    public static Range day(LocalDate day) {
        return new Range(day, day, Format.date(day));
    }

    /**
     * Create a range for a week, starting with a given day.
     */
    public static Range week(LocalDate day) {
        var txt = "week " + day.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        return new Range(day, day.plusDays(6), txt);
    }

    /**
     * Create a range for a month.
     */
    public static Range month(YearMonth ym) {
        var start = LocalDate.of(ym.getYear(), ym.getMonth(), 1);
        var end = LocalDate.of(ym.getYear(), ym.getMonth(), ym.lengthOfMonth());
        return new Range(start, end, ym.getMonth() + " " + ym.getYear());
    }

    /**
     * Create a range for a year.
     */
    public static Range year(int year) {
        var start = LocalDate.of(year, Month.JANUARY, 1);
        var end = LocalDate.of(year, Month.DECEMBER, 31);
        return new Range(start, end, year + "");
    }

    /**
     * Filter for {@code LocalDate} objects that are in the range.
     */
    public boolean filter(LocalDate ld) {
        return !start.isAfter(ld) && !end.isBefore(ld);
    }

    /**
     * Filter on {@code LocalDateTime} objects that are in the range.
     */
    public boolean filter(LocalDateTime ldt) {
        return filter(ldt.toLocalDate());
    }

    /**
     * Filter on {@code Activity} objects that start in the range.
     */
    public boolean filter(Session act) {
        return filter(act.start().toLocalDate());
    }

}
