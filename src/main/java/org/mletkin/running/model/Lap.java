package org.mletkin.running.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.mletkin.garmin.ActivityLapT;
import org.mletkin.garmin.TrackpointT;
import org.mletkin.running.util.Util;

/**
 * Provides the information from an {@link ActivityLapT} xml element.
 */
public class Lap {

    private LocalDateTime start;
    private Duration time;
    private Distance distance;
    private List<Trackpoint> track = new ArrayList<>();

    /**
     * Creates a new {@code Lap} object.
     *
     * @param actLap
     *                       {@code ActivityLapT} object to fill from
     * @param preceeding
     *                       last {@code Trackpoint} from the preceeding lap
     */
    public Lap(ActivityLapT actLap, Trackpoint preceeding) {
        this.distance = Distance.meter(actLap.getDistanceMeters());
        this.time = Duration.ofMillis((long) actLap.getTotalTimeSeconds() * 1000);
        this.start = Util.toLocalDateTime(actLap.getStartTime());

        addTrackpoints(actLap, preceeding);
    }

    /**
     * Entry point for normalized Laps.
     */
    protected Lap() {
        // intentially left empty
    }

    private void addTrackpoints(ActivityLapT actLap, Trackpoint starting) {
        Trackpoint last = starting;
        for (var track : actLap.getTrack()) {
            for (var tp : track.getTrackpoint()) {
                last = addTrackpoint(last, tp);
            }
        }
    }

    private Trackpoint addTrackpoint(Trackpoint last, TrackpointT tpt) {
        var tp = new Trackpoint.Builder(tpt).withPrevious(last).build();
        track.add(tp);
        return tp;
    }

    /**
     * Returns the distance run in the lap.
     */
    public Distance distance() {
        return distance;
    }

    /**
     * Returns the time the session took.
     */
    public Duration time() {
        return time;
    }

    /**
     * Returns the time the lap started.
     */
    public LocalDateTime start() {
        return start;
    }

    /**
     * Returns a {@coede Stream} of all the {@code Trackpoint} objects of the lap.
     */
    public Stream<Trackpoint> track() {
        return track.stream();
    }

    Trackpoint lastTrackpoint() {
        return track.isEmpty() ? null : track.getLast();
    }

    /**
     * Returns the total vertical distance "up" in the lap.
     */
    public Distance deltaAlt() {
        return track() //
                .map(Trackpoint::deltaAltitude) //
                .filter(Distance::positive) //
                .reduce(Distance.ZERO, Distance::plus);
    }
}
