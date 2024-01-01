package org.mletkin.running.gui.prep;

import java.time.Duration;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.mletkin.running.model.Distance;
import org.mletkin.running.model.Speed;
import org.mletkin.running.model.Trackpoint;

/**
 * Smoothes curves by calculating the average over multiple samples.
 */
public class Smoother {

    private CircularFifoQueue<Trackpoint> line;

    /**
     * Create an smoothing object.
     *
     * @param size
     *                 number of samples to combine
     */
    public Smoother(int size) {
        this.line = new CircularFifoQueue<>(size);
    }

    /**
     * Adds another sample to the buffer.
     *
     * @param element
     *                    track point to add
     * @return modified {@code Smother} object
     */
    public Smoother add(Trackpoint element) {
        line.add(element);
        return this;
    }

    /**
     * Returns the calculated speed.
     *
     * @return Speed object
     */
    public Speed value() {
        var dist = Distance.ZERO;
        var time = Duration.ZERO;
        for (var n = 0; n < line.size(); n++) {
            dist = dist.plus(line.get(n).deltaDistance());
            time = time.plus(line.get(n).deltaTime());
        }
        return new Speed(time, dist);
    }

}
