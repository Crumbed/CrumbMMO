package com.crumbed.crumbmmo.stats;

public class BigStat extends Stat.Value {
    public Stat.Value max;
    public Stat.Value regen;

    public BigStat(
        double value,
        double max,
        double regen
    ) {
        super(value);
        this.max = new Stat.Value(max);
        this.regen = new Stat.Value(regen);
    }
}
