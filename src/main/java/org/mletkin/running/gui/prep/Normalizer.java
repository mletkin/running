package org.mletkin.running.gui.prep;

import org.mletkin.running.model.Activity;

public class Normalizer {

    public Activity norm(Activity raw) {
        if (raw == null) {
            return null;
        }
        var result = new NormActivity();
        result.start = raw.start();
        result.time = raw.time();
        result.dist = calcDist(raw);
        return result;
    }

    private double calcDist(Activity raw) {
        return 200 + 700 * calcLapNum(raw);
    }

    private int calcLapNum(Activity raw) {
        return (int) ((raw.dist() - 200) / 700 + 0.5);
    }
}
