package org.mletkin.running.gui.prep;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.mletkin.running.model.Activity;
import org.mletkin.running.model.Lap;

public class NormActivity extends Activity {

    LocalDateTime start;
    Duration time;
    double dist;

    public NormActivity() {
        super(null);
    }

    @Override
    public String sport() {
        return "Running";
    }

    @Override
    public LocalDateTime start() {
        return start;
    }

    @Override
    public Stream<Lap> laps() {
        return Stream.of();
    }

    @Override
    public Duration time() {
        return time;
    }

    @Override
    public double dist() {
        return dist;
    }
}
