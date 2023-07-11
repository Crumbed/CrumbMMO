package com.crumbed.crumbmmo.stats;

public class DamageValue {
    private final int value;
    private final boolean crit;
    private final DamageType dt;

    public DamageValue(int value, boolean crit, DamageType dt) {
        this.value = value;
        this.crit = crit;
        this.dt = dt;
    }

    public int getDamage() { return value; }
    public boolean isCrit() { return crit; }
    public DamageType getDamageType() { return dt; }
}
