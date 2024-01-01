package org.mletkin.running.model;

import java.time.Duration;

public record Speed(Duration time, Distance dist) {

    public static Speed ZERO = new Speed(Duration.ZERO, Distance.ZERO);

    public Duration pace() {
        return dist.meter() > 0 ? Duration.ofSeconds((long) (1.0 * time.toMillis() / dist.meter())) : Duration.ZERO;
    }

    public double kmh() {
        return time().getSeconds() > 0 ? dist.km() / hours(time()) : 0;
    }

    public double secPerKm() {
        return dist.km() > 0 ? time().getSeconds() / dist.km() : 10_000;
    }

    private static double hours(Duration time) {
        return time.toSeconds() / 3600.0;
    }

}
