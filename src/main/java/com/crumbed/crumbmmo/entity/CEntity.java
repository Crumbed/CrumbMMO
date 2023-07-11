package com.crumbed.crumbmmo.entity;

import com.crumbed.crumbmmo.utils.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CEntity {
    public transient UUID id;
    private transient ArrayList<EntityComponent> components;

    public CEntity(EntityComponent... components) {
        this.id = UUID.randomUUID();
        this.components = (ArrayList<EntityComponent>) Arrays.asList(components);
    }

    public boolean hasComponents(EntityComponent.ComponentType... filter) {
        return hasComponents(Arrays.asList(filter));
    }
    public boolean hasComponents(List<EntityComponent.ComponentType> query) {
        for (EntityComponent.ComponentType type : query) {
            if (this.components
                    .stream()
                    .noneMatch(c -> c.getType().equals(type)))
                return false;
        }
        return true;
    }

    public Option<List<EntityComponent>> getComponents(EntityComponent.ComponentType... filter) {
        List<EntityComponent> comps = this.components
                .stream()
                .filter(c -> Arrays.stream(filter)
                        .anyMatch(type -> type.equals(c.getType())))
                .collect(Collectors.toList());

        if (filter.length != comps.size()) return Option.none();
        return Option.some(comps);
    }

    public void addComponent(EntityComponent comp) { this.components.add(comp); }
    public Option<EntityComponent> getComponent(EntityComponent.ComponentType type) {
        List<EntityComponent> comp = components
                .stream()
                .filter(c -> c.getType().equals(type))
                .collect(Collectors.toList());
        if (comp.size() == 0) return Option.none();
        return Option.some(comp.get(0));
    }

    public void initLoaded() {
        id = UUID.randomUUID();
        components = new ArrayList<>();
    }

    public void update() {}

}





























