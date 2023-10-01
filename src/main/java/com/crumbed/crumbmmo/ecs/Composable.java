package com.crumbed.crumbmmo.ecs;

import com.crumbed.crumbmmo.utils.Option;

import java.util.ArrayList;

/*
* Interface for anything that contains EntityComponents
*/
public interface Composable {
    default <T extends EntityComponent> boolean hasComponent(Class<T> component) {
        int componentId;
        try {
            /*
             * Gets the static Field "ID" from the Class<T extends EntityComponent>
             * provided. Then we extract the value as an int.
             */
            componentId = component.getField("ID").getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }

        return hasComponent(componentId);
    }
    boolean hasComponent(int componentId);

    <T extends EntityComponent> Option<T> getComponent(Class<T> componentType);

    static int binarySearch(ArrayList<EntityComponent> comps, int targetId) {
        int l = 0, r = comps.size() - 1;
        if (comps.get(r).id() == targetId) return r;
        while (l <= r) {
            int m = l + (r - 1) / 2;

            int compID = comps.get(m).id();
            if (compID == targetId) return m;
            if (compID < targetId)
                l = m + 1;
            else
                r = m - 1;
        }

        return -1;
    }
}



















