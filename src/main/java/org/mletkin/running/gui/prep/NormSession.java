package org.mletkin.running.gui.prep;

import java.time.Duration;
import java.time.LocalDateTime;

import org.mletkin.running.model.Distance;
import org.mletkin.running.model.Session;

public class NormSession extends Session {

    Duration time;
    Distance dist;

    public NormSession(LocalDateTime start, int lapCount, Distance altPerLap) {
        super(null);
        for (var n = 0; n < lapCount; n++) {
            laps.add(new NormLap(altPerLap));
        }
        this.start = start;
    }

    @Override
    public LocalDateTime start() {
        return start;
    }

    @Override
    public Duration time() {
        return time;
    }

    @Override
    public Distance distance() {
        return dist;
    }

}
