package net.minedo.mc.constants.regenerationthreshold;

public enum RegenerationThreshold {

    THRESHOLD(70);

    private final double threshold;

    RegenerationThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getThreshold() {
        return threshold;
    }
}
