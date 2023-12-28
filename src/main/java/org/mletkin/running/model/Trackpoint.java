package org.mletkin.running.model;

import java.time.Duration;
import java.time.LocalDateTime;

import org.mletkin.garmin.TrackpointT;
import org.mletkin.running.util.Util;

/**
 * Wraps the {@link TrackpointT} xml element.
 * <p>
 * Delta values are calculated from the predecing trackpoint.<br>
 * Thus the first track point in the lap has all delta on zero.
 */
public class Trackpoint {

    private TrackpointT tp;
    private LocalDateTime time;
    private Distance dAlt = Distance.ZERO;
    private Distance dDist = Distance.ZERO;
    private Duration dTime = Duration.ZERO;

    public Trackpoint(TrackpointT tp) {
        this.time = Util.toLocalDateTime(tp.getTime());
        this.tp = tp;
    }

    public LocalDateTime time() {
        return time;
    }

    private Double altitudeMeters() {
        return tp.getAltitudeMeters();
    }

    public int cadence() {
        return tp.getCadence();
    }

    private Double distanceMeters() {
        return tp.getDistanceMeters();
    }

    public Distance deltaAltitude() {
        return dAlt;
    }

    public Distance deltaDistance() {
        return dDist;
    }

    public Duration deltaTime() {
        return dTime;
    }

    public Speed speed() {
        return new Speed(dTime, dDist);
    }

    static class Builder {

        private Trackpoint tp;

        Builder(TrackpointT tpt) {
            this.tp = new Trackpoint(tpt);
        }

        Builder withPrevious(Trackpoint last) {
            if (last != null) {
                if (last.distanceMeters() != null && tp.distanceMeters() != null) {
                    tp.dDist = Distance.meter(tp.distanceMeters() - last.distanceMeters());
                }
                if (last.altitudeMeters() != null && tp.altitudeMeters() != null) {
                    tp.dAlt = Distance.meter(tp.altitudeMeters() - last.altitudeMeters());
                }
                tp.dTime = Duration.between(last.time(), tp.time());
            }
            return this;
        }

        Trackpoint build() {
            return tp;
        }

    }
}
