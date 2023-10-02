package com.crumbed.crumbmmo.utils;

public abstract class Timeable {
    public abstract int getRemainingTicks();
    public abstract void subtractTicks(int ticks);
    public abstract void afterTimer();
}
