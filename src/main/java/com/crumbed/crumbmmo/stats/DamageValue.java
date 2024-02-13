package com.crumbed.crumbmmo.stats;

public class DamageValue {
    private int value;
    private final boolean crit;
    private final DamageType dt;

    public DamageValue(int value, boolean crit, DamageType dt) {
        this.value = value;
        this.crit = crit;
        this.dt = dt;
    }

    public void reduceDamage(double reduction) {
        value -= reduction * value;
    }
    public void setDamage(int value) { this.value = value; }
    public int getDamage() { return value; }
    public boolean isCrit() { return crit; }
    public DamageType getDamageType() { return dt; }
}
