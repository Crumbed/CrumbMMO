package com.crumbed.crumbmmo.ecs;

import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class ComponentQuery {
    public ArrayList<Integer> compIds;

    @SafeVarargs
    public ComponentQuery(Class<? extends EntityComponent>... comps) {
        compIds = Arrays.stream(comps) // Loop through all component classes
                .map(x -> { // change each component to its "ID"
                    int compId = -1;
                    try {
                        compId = x.getField("ID").getInt(null); // Extract the value of "ID"
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return compId;
                })
                .sorted() // Sort ids
                .collect(Collectors.toCollection(ArrayList::new)); // Collect into an ArrayList<Integer>
    }

    public Result collect(int entityId, ArrayList<EntityComponent> collected) {
        ArrayList<EntityComponent> comps = new ArrayList<>();
        for (int id : compIds) {
            int index = Composable.binarySearch(collected, id);
            if (index < 0) return new Result();

            Bukkit.getLogger().info("query picked component at: " + index + ", with id: " + id);
            comps.add(collected.get(index));
        }

        return new Result((ArrayList<Integer>) compIds.clone(), comps, entityId);
    }


    public static class Result implements Composable {
        public final boolean complete;
        private final ArrayList<Integer> compIds;
        private final ArrayList<EntityComponent> comps;
        public final int parentEntity;

        private Result() {
            complete = false;
            compIds = null;
            comps = null;
            parentEntity = -1;
        }

        private Result(
                ArrayList<Integer> compIds,
                ArrayList<EntityComponent> comps,
                int entityId
        ) {
            this.complete = true;
            this.compIds = compIds;
            this.comps = comps;
            parentEntity = entityId;
        }

        @Override
        public boolean hasComponent(int componentId) {
            int i = Collections.binarySearch(compIds, componentId);
            return i >= 0;
        }

        public <T extends EntityComponent> Option<T> getComponent(Class<T> componentType) {
            int compId = -1;
            try {
                compId = componentType.getField("ID").getInt(null); // Extract the value of "ID"
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            int compIndex = Collections.binarySearch(compIds, compId);

            return (compIndex < 0)
                    ? Option.none()
                    : Option.some((T) comps.get(compIndex));
        }
    }
}





















