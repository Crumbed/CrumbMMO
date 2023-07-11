package com.crumbed.crumbmmo.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class EntitySystem {
    private final List<EntityComponent.ComponentType> query;
    public final int tickFreq;
    public int callCount;

    public EntitySystem(int tickFreq, EntityComponent.ComponentType... query) {
        this.query = Arrays.asList(query);
        this.tickFreq = tickFreq;
        this.callCount = 0;
    }

    public List<EntityComponent.ComponentType> getQuery() { return query; }

    public void execute(Stream<CEntity> entities) {}
}
