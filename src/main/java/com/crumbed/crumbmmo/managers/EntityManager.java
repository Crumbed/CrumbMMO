package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.ComponentQuery;
import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.crumbed.crumbmmo.ecs.EntitySystem;
import com.crumbed.crumbmmo.ecs.components.EntityActionBar;
import com.crumbed.crumbmmo.ecs.systems.StatRegen;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Manager for all thing related to entities
 */
public class EntityManager {
    public static EntityManager INSTANCE = null;
    /**
     * List of all registered entities.
     * Each entity ID is its index in the list
     */
    private ArrayList<CEntity> entities;

    /**
     * All registered systems
     */
    private final ArrayList<EntitySystem> systems;

    private EntityManager(ArrayList<EntitySystem> systems) {
        entities = new ArrayList<>();
        this.systems = systems;
    }


    public void addEntity(CEntity entity) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i) != null) continue;
            entities.set(i, entity);
            entity.id = i;
            Bukkit.getLogger().info("Added entity with id: " + i);
            return;
        }

        Bukkit.getLogger().info("Added entity with id: " + entities.size());
        entity.id = entities.size();
        entities.add(entity);
    }

    public void killEntity(int id) {
        if (id < 0 || id >= entities.size()) {
            Bukkit.getServer().getConsoleSender().sendMessage(String.format(
                    "Entity id: %s, is outside the range of entities: 0 - %s",
                    id, entities.size() - 1
            ));
            return;
        }
        entities.set(id, null);
    }

    public Option<CEntity> getEntity(int id) {
        if (id < 0 || id >= entities.size()) return Option.none();
        CEntity e = entities.get(id);
        return (e == null)
                ? Option.none()
                : Option.some(e);
    }

    public Stream<CEntity> getEntities() {
        return entities.stream();
    }


    public void update(CrumbMMO plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (EntitySystem sys : systems) {
                    sys.callCount += 1;
                    if (sys.tickFreq != sys.callCount) continue;

                    ComponentQuery query = sys.getQuery();
                    Stream<ComponentQuery.Result> results = entities.stream()
                            .filter(Objects::nonNull)
                            .map(x -> x.match(query))
                            .filter(x -> x.complete);
                    sys.execute(results);
                    sys.callCount = 0;
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public boolean containsEntity(int id) {
        return entities.get(id) != null;
    }


    public static class Builder {
        private ArrayList<EntitySystem> systems;
        private int componentCount;

        public Builder() {
            systems = new ArrayList<>();
            componentCount = 0;
        }

        public Builder withSystem(EntitySystem sys) {
            systems.add(sys);
            return this;
        }

        public <T extends EntityComponent> Builder withComponent(Class<T> component) {
            try {
                component.getField("ID").setInt(null, componentCount);
                componentCount += 1;
            } catch(NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return this;
        }

        public EntityManager create() {
            return new EntityManager(systems);
        }
    }
}




































