package org.mletkin.running.gui.main;

import java.time.Duration;

import org.mletkin.running.model.Activity;

/**
 * Summarize data für a set of {@code Activities}.
 */
class Summarizer {

    private double dist = 0;
    private Duration time = Duration.ZERO;
    private long laps = 0;
    private long runs = 0;

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

    /**
     * Accumulated distance in meters.
     */
    public double dist() {
        return dist;
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