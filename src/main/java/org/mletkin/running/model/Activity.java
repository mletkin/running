package org.mletkin.running.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.mletkin.garmin.ActivityLapT;
import org.mletkin.garmin.ActivityT;
import org.mletkin.running.util.Util;

/**
 * Wraps the {@link ActivityT} xml element.
 */
public class Activity {

    private ActivityT act;
    private List<Lap> laps = new ArrayList<>();

    public Activity(ActivityT act) {
        this.act = act;
        addLaps(act);
    }

    private void addLaps(ActivityT act) {
        Trackpoint last = null;
        for (var lap : act.getLap()) {
            last = addLap(lap, last);
        }
    }

    private Trackpoint addLap(ActivityLapT lapT, Trackpoint last) {
        var lap = new Lap(lapT, last);
        laps.add(lap);
        return lap.lastTrackpoint();
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
