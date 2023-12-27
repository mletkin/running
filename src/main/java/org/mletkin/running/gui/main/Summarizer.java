package org.mletkin.running.gui.main;

import java.time.Duration;

import org.mletkin.running.model.Activity;
import org.mletkin.running.model.Trackpoint;

/**
 * Summarize data für a set of {@code Activities}.
 */
public class Summarizer {

    private double dist = 0.0;
    private Duration time = Duration.ZERO;
    private long laps = 0;
    private long runs = 0;
    private double altUp = 0.0;
    private double altDown = 0.0;

    /**
     * add the data from an activity.
     *
     * @param run
     *                activity to process
     */
    public void add(Activity run) {
        dist += run.dist() / 1000;
        time = time.plus(run.time());
        laps += run.laps().count();
        runs++;
    }

    public void add(Trackpoint tp) {
        dist += tp.deltaDistance();
        time = time.plus(tp.deltaTime());
        if (tp.deltaAltitude() > 0) {
            altUp += tp.deltaAltitude();
        } else {
            altDown -= tp.deltaAltitude();
        }
    }

    /**
     * Accumulated distance in meters.
     */
    public double dist() {
        return dist;
    }

    /**
     * Accumulated ascending in meters.
     */
    public double altUp() {
        return altUp;
    }

    public double altDown() {
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