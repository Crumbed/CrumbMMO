package com.crumbed.crumbmmo.ecs;

import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.utils.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class CEntity implements Composable {
    public int id;
    private final ArrayList<EntityComponent> components;

    //IF YOU USE THIS CONSTRUCTOR MAKE SURE THE COMPONENTS ARE SORTED PROPERLY
    public CEntity(EntityComponent... comps) {
        this.id = -1;
        this.components = new ArrayList<>(Arrays.asList(comps));
    }
    private CEntity(ArrayList<EntityComponent> comps) {
        this.id = -1;
        this.components = comps;
    }


    public boolean hasComponent(int componentId) {
        // preform a binary search using the component ids
        int index = Composable.binarySearch(components, componentId);
        return index >= 0;
    }

    @Override
    public <T extends EntityComponent> Option<T> getComponent(Class<T> componentType) {
        int compId;
        try {
            compId = componentType.getField("ID").getInt(null); // Extract the value of "ID"
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return Option.none();
        }

        for (EntityComponent c : components) {
            if (c.id() != compId) continue;
            return Option.some((T) c);
        }
        return Option.none();
    }

    public ComponentQuery.Result match(ComponentQuery query) {
        return query.collect(id, this.components);
    }

    public String displayComponents() {
        return components.stream()
                .map(EntityComponent::id)
                .map(x -> x + "\n")
                .collect(Collectors.joining());
    }

    public static class Builder {
        private ArrayList<EntityComponent> components;

        public Builder() { components = new ArrayList<>(); }

        public Builder with(EntityComponent c) {
            int i = 0;
            // makes sure that when we add components
            // they are sorted by their id
            for (; i < components.size(); i++) {
                EntityComponent cI = components.get(i);
                if (c.id() <= cI.id()) break;
            }

            components.add(i, c);
            return this;
        }

        public CEntity create(EntityManager ecs) {
            CEntity e = new CEntity(components);
            ecs.addEntity(e);
            return e;
        }

        public CEntity shadowCreate() {
            return new CEntity(components);
        }
    }
}





























