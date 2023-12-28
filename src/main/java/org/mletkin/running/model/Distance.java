package org.mletkin.running.model;

public record Distance(double meter) {

    public static final Distance ZERO = new Distance(0);

    public static Distance meter(double meter) {
        return new Distance(meter);
    }

    static Distance kilometer(double kilometer) {
        return new Distance(kilometer * 1000);
    }

    public Distance plus(Distance other) {
        return meter(this.meter + other.meter);
    }

    public Distance minus(Distance other) {
        return meter(this.meter - other.meter);
    }

    public boolean positive() {
        return meter > 0;
    }

    public double km() {
        return meter / 1000;
    }
}
