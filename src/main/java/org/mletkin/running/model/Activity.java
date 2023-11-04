package org.mletkin.running.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mletkin.garmin.ActivityT;
import org.mletkin.running.util.Util;

/**
 * Wraps the {@link ActivityT} xml element.
 */
public class Activity {

    private ActivityT act;
    private List<Lap> laps;

    public Activity(ActivityT act) {
        this.act = act;
        this.laps = act.getLap().stream() //
                .map(Lap::new) //
                .collect(Collectors.toList());
    }

    public String sport() {
        return act.getSport().value();
    }

    public LocalDateTime start() {
        return Util.toLocalDateTime(act.getId());
    }

    public Stream<Lap> laps() {
        return laps.stream();
    }

    public Duration time() {
        return laps().map(Lap::time).reduce(Duration.ZERO, Duration::plus);
    }

    public double dist() {
        return laps().mapToDouble(Lap::meter).sum();
    }
}
