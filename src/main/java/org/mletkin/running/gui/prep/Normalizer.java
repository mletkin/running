package org.mletkin.running.gui.prep;

import org.mletkin.running.model.Distance;
import org.mletkin.running.model.Session;

public class Normalizer {

    public Session norm(Session raw) {
        if (raw == null) {
            return null;
        }
        var result = new NormSession(raw.start(), calcLapNum(raw), Distance.meter(8));
        result.time = raw.time();
        result.dist = calcDist(raw);
        return result;
    }

    private Distance calcDist(Session raw) {
        return Distance.meter(200 + 700 * calcLapNum(raw));
    }

    private int calcLapNum(Session raw) {
        return (int) ((raw.distance().meter() - 200) / 700 + 0.5);
    }
}
