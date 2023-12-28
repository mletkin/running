package org.mletkin.running.model;

import java.time.Duration;

public record Speed(Duration time, Distance dist) {

    public Duration pace() {
        return Duration.ofSeconds((long) (1.0 * time.toMillis() / dist.meter()));
    }

    public double kmh() {
        return time().getSeconds() > 0 ? dist.meter() / hours(time()) : 0;
    }

    private static double hours(Duration time) {
        return time.toSeconds() / 3600.0;
    }
}
