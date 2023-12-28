package org.mletkin.running.gui.main;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import org.mletkin.running.model.Session;
import org.mletkin.running.model.Lap;
import org.mletkin.running.model.Trackpoint;

/**
 * Map Activity Trackpoints to pace groups.
 *
 * This is merely a proof of concept.
 * <ul>
 * <li>The ranges are hard wired
 * <li>The groups form a partition on the range
 * <li>The trackpoint-time is hard wired to 1s
 * <li>group 0 is for speed zero
 * </ul>
 *
 */
public class SpeedGrouper {

    private Map<Integer, Duration> map = new HashMap<>();

    public record Line(int group, Duration time, double percentage) {
        // sufficient
    }

    public void process(Session run) {
        run.laps().flatMap(Lap::track).forEach(this::process);
    }

    public void process(Trackpoint tp) {
        add(tp.deltaDistance().meter(), tp.deltaTime().getSeconds());
    }

    private void add(double meter, long seconds) {
        var group = (Math.abs(meter) < 1) ? 0 : group((long) (seconds * 1000 / meter));
        var dur = Optional.ofNullable(map.get(group)).orElse(Duration.ZERO);
        map.put(group, dur.plusSeconds(seconds));
    }

    /**
     * Map pace to pace group.
     *
     * @param pace
     *                 seconds / km
     * @return group 5, the higher the faster
     */
    private int group(long pace) {
        if (pace < 240) {
            return 5; // 4min
        }
        if (pace < 300) {
            return 4; // 5min
        }
        if (pace < 360) {
            return 3; // 6min
        }
        if (pace < 420) {
            return 2; // 7min
        }
        if (pace < 600) {
            return 1; // 10min
        }
        return 0;
    }

    private Duration sum() {
        return map.values().stream().reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Get the result as a list of Line records.
     */
    public Stream<Line> result() {
        var sum = sum();
        return map.entrySet().stream() //
                .map(e -> mkLine(e, sum));
    }

    private Line mkLine(Entry<Integer, Duration> e, Duration complete) {
        var percent = 1.0 * e.getValue().toSeconds() / complete.toSeconds();
        return new Line(e.getKey(), e.getValue(), percent);
    }

}
