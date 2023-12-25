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
 * Wraps the {@link ActivityLapT} xml element.
 */
public class Lap {

    private ActivityLapT actLap;
    private List<Trackpoint> track = new ArrayList<>();

    public Lap(ActivityLapT actLap, Trackpoint starting) {
        this.actLap = actLap;
        addTrackpoints(actLap, starting);
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

    public double meter() {
        return actLap.getDistanceMeters();
    }

    public long seconds() {
        return (long) actLap.getTotalTimeSeconds();
    }

    public long milliSeconds() {
        return (long) actLap.getTotalTimeSeconds() * 1000;
    }

    public Duration pace() {
        return Duration.ofSeconds((long) (milliSeconds() / meter()));
    }

    public Duration time() {
        return Duration.ofMillis(milliSeconds());
    }

    public int bpmAvg() {
        var bpm = actLap.getAverageHeartRateBpm();
        return bpm == null ? 0 : bpm.getValue();
    }

    public LocalDateTime start() {
        return Util.toLocalDateTime(actLap.getStartTime());
    }

    public Stream<Trackpoint> track() {
        return track.stream();
    }

    Trackpoint lastTrackpoint() {
        return track.isEmpty() ? null : track.getLast();
    }
}
