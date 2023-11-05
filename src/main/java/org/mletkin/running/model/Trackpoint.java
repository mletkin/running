package org.mletkin.running.model;

import java.time.LocalDateTime;

import org.mletkin.garmin.TrackpointT;
import org.mletkin.running.util.Util;

/**
 * Wraps the {@link TrackpointT} xml element.
 */
public class Trackpoint {

    private TrackpointT tp;

    public Trackpoint(TrackpointT tp) {
        this.tp = tp;
    }

    public LocalDateTime time() {
        return Util.toLocalDateTime(tp.getTime());
    }

    public double altitude() {
        return tp.getAltitudeMeters();
    }

    public Double distance() {
        return tp.getDistanceMeters();
    }

    public int cadence() {
        return tp.getCadence();
    }

}
