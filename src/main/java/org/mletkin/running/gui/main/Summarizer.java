package org.mletkin.running.gui.main;

import java.time.Duration;

import org.mletkin.running.model.Distance;
import org.mletkin.running.model.Session;
import org.mletkin.running.model.Trackpoint;

/**
 * Summarize data für a set of {@code Activities}.
 */
public class Summarizer {

    private Distance dist = Distance.ZERO;
    private Duration time = Duration.ZERO;
    private long laps = 0;
    private long runs = 0;
    private Distance altUp = Distance.ZERO;
    private Distance altDown = Distance.ZERO;

    /**
     * add the data from an activity.
     *
     * @param session
     *                    activity to process
     */
    public void add(Session session) {
        dist = dist.plus(session.distance());
        time = time.plus(session.time());
        laps += session.laps().count();
        altUp = altUp.plus(session.altUp());
        runs++;
    }

    public void add(Trackpoint tp) {
        dist = dist.plus(tp.deltaDistance());
        time = time.plus(tp.deltaTime());
        if (tp.deltaAltitude().positive()) {
            altUp = altUp.plus(tp.deltaAltitude());
        } else {
            altDown = altDown.minus(tp.deltaAltitude());
        }
    }

    /**
     * Accumulated distance in meters.
     */
    public Distance dist() {
        return dist;
    }

    /**
     * Accumulated ascending in meters.
     */
    public Distance altUp() {
        return altUp;
    }

    public Distance altDown() {
        return altDown;
    }

    /**
     * Accumulated time.
     */
    public Duration time() {
        return time;
    }

    /**
     * Number of laps.
     */
    public long laps() {
        return laps;
    }

    /**
     * Number of processed activities.
     */
    public long runs() {
        return runs;
    }

}