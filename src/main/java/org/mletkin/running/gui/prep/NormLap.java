package org.mletkin.running.gui.prep;

import org.mletkin.running.model.Distance;
import org.mletkin.running.model.Lap;

public class NormLap extends Lap {

    private Distance deltaAlt;

    NormLap(Distance deltaAlt) {
        super();
        this.deltaAlt = deltaAlt;
    }

    @Override
    public Distance deltaAlt() {
        return deltaAlt;
    }
}
