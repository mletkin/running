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
 * Represents a running session.
 * <p>
 * Basically wraps the data from an {@link ActivityT} xml element.
 */
public class Session {

    protected LocalDateTime start;
    protected List<Lap> laps = new ArrayList<>();

    /**
     * Converts an activity to a session.
     *
     * @param act
     *                {@link ActivityT} object
     */
    public Session(ActivityT act) {
        if (act != null) {
            this.start = Util.toLocalDateTime(act.getId());
            this.addLaps(act);
        }
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

    /**
     * Time the session started.
     *
     * @return {@code LocalDateTime} object
     */
    public LocalDateTime start() {
        return start;
    }

    /**
     * Returns all the laps in the session.
     *
     * @return {@code Stream} of {@code Lap} objects
     */
    public Stream<Lap> laps() {
        return laps.stream();
    }

    /**
     * Returns the number of laps in the session.
     *
     * @return number of laps
     */
    public int lapCount() {
        return laps.size();
    }

    /**
     * Returns the running time of the session.
     *
     * @return Accumulated lap running times.
     */
    public Duration time() {
        return laps().map(Lap::time).reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Returns the distance run in the session.
     *
     * @return Accumulated lap running distances.
     */
    public Distance distance() {
        return laps().map(Lap::distance).reduce(Distance.ZERO, Distance::plus);
    }

    public Distance altUp() {
        return laps().map(Lap::deltaAlt).reduce(Distance.ZERO, Distance::plus);
    }
}
