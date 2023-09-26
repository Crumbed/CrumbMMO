package com.crumbed.crumbmmo.ecs;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class EntitySystem {
    private final ComponentQuery query;
    public final int tickFreq;
    public int callCount;

    public EntitySystem(int tickFreq, ComponentQuery query) {
        this.query = query;
        this.tickFreq = tickFreq;
        this.callCount = 0;
    }

    public ComponentQuery getQuery() { return query; }

    public abstract void execute(Stream<ComponentQuery.Result> results);
}
