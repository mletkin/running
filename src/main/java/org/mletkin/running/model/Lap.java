package org.mletkin.running.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mletkin.garmin.ActivityLapT;
import org.mletkin.running.util.Util;

/**
 * Wraps the {@link ActivityLapT} xml element.
 */
public class Lap {

    private ActivityLapT actLap;
    private List<Trackpoint> track;

    public Lap(ActivityLapT actLap) {
        this.actLap = actLap;
        track = addTrack(actLap).collect(Collectors.toList());
    }

    private Stream<Trackpoint> addTrack(ActivityLapT actLap) {
        return actLap.getTrack().stream() //
                .flatMap(t -> t.getTrackpoint().stream()) //
                .map(Trackpoint::new);
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
}
