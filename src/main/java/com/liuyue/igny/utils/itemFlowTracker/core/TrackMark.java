package com.liuyue.igny.utils.itemFlowTracker.core;

public class TrackMark {
    private final int rgb;
    private final String label;
    private final int pathInterval;
    private final int generation;
    private final int capacity;

    private int budget;
    private int exhaustedTicks;
    private boolean retired;

    TrackMark(int rgb, String label, int generation, int capacity, int pathInterval) {
        this.rgb = rgb & 0xFFFFFF;
        this.label = label;
        this.pathInterval = Math.max(0, pathInterval);
        this.generation = generation;
        this.capacity = Math.max(1, capacity);
        this.budget = this.capacity;
    }

    public int rgb() {
        return this.rgb;
    }

    public String label() {
        return this.label;
    }

    public int pathInterval() {
        return this.pathInterval;
    }

    public int capacity() {
        return this.capacity;
    }

    public int budget() {
        return this.budget;
    }

    int generation() {
        return this.generation;
    }

    boolean isRetired() {
        return this.retired;
    }

    void spend(int amount) {
        this.budget -= amount;
    }

    void refund(int amount) {
        this.budget = Math.min(this.capacity, this.budget + amount);
    }

    void retire() {
        this.retired = true;
    }

    int tickExhausted() {
        if (this.budget > 0) {
            this.exhaustedTicks = 0;
            return 0;
        }

        return ++this.exhaustedTicks;
    }

    @Override
    public String toString() {
        return "TrackMark[" + this.label + " " + this.budget + "/" + this.capacity + "]";
    }
}
