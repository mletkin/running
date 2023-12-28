package org.mletkin.running.gui.main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.mletkin.running.model.Distance;
import org.mletkin.running.model.Lap;
import org.mletkin.running.model.Session;
import org.mletkin.running.model.Trackpoint;

/**
 * Map Trackpoints to altitude groups.
 * <p>
 * Used to find the average altitude in a lap.
 */
public class AltitudeGrouper {

    private Map<Distance, Long> map = new HashMap<>();

    public record Line(Distance group, long count) {

        public static Comparator<Line> compare() {
            return (Line a, Line b) -> Double.compare(a.group.meter(), b.group.meter());
        }

        public static Comparator<Line> compareCount() {
            return (Line a, Line b) -> Long.compare(a.count, b.count);
        }

        int compareTo(Line other) {
            return Double.compare(this.group.meter(), other.group.meter());
        }
    }

    public void process(Session run) {
        run.laps().flatMap(Lap::track).forEach(this::process);
    }

    public void process(Trackpoint tp) {
        add(tp.deltaAltitude());
    }

    public void add(Distance value) {
        var dur = Optional.ofNullable(map.get(value)).orElse(0L);
        map.put(value, ++dur);
    }

    /**
     * Get the result as a list of Line records.
     */
    public Stream<Line> result() {
        return map.entrySet().stream() //
                .map(e -> new Line(e.getKey(), e.getValue()));
    }

}
