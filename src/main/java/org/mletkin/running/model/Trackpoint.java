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
    private double dAlt = 0;
    private double dDist = 0;
    private Duration dTime = Duration.ZERO;

    public Trackpoint(TrackpointT tp) {
        this.tp = tp;
    }

    public LocalDateTime time() {
        return Util.toLocalDateTime(tp.getTime());
    }

    public Double altitude() {
        return tp.getAltitudeMeters();
    }

    public int cadence() {
        return tp.getCadence();
    }

    public Double distance() {
        return tp.getDistanceMeters();
    }

    public double deltaAltitude() {
        return dAlt;
    }

    public double deltaDistance() {
        return dDist;
    }

    public Duration deltaTime() {
        return dTime;
    }

    public Duration pace() {
        return Duration.ofSeconds((long) (dTime.toMillis() / deltaDistance()));
    }

    static class Builder {

        private Trackpoint tp;

        Builder(TrackpointT tpt) {
            this.tp = new Trackpoint(tpt);
        }

        Builder withPrevious(Trackpoint last) {
            if (last != null) {
                if (last.distance() != null && tp.distance() != null) {
                    tp.dDist = tp.distance() - last.distance();
                }
                if (last.altitude() != null && tp.altitude() != null) {
                    tp.dAlt = tp.altitude() - last.altitude();
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
