package com.crumbed.crumbmmo.managers;

import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.entity.CEntity;
import com.crumbed.crumbmmo.entity.EntityComponent;
import com.crumbed.crumbmmo.entity.EntitySystem;
import com.crumbed.crumbmmo.entity.systems.ActionBarSystem;
import com.crumbed.crumbmmo.entity.systems.PlayerInvUpdate;
import com.crumbed.crumbmmo.entity.systems.StatRegen;
import com.crumbed.crumbmmo.entity.systems.SyncHealthTypes;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Manager for all thing related to entities
 */
public class EntityManager {
    public static Option<EntityManager> INSTANCE = Option.none();
    /**
     * Map from Entity ID -> CEntity
     */
    private HashMap<UUID, CEntity> entities;

    /**
     * All registered systems
     */
    private static final EntitySystem SYSTEMS[] = {
            new StatRegen(),
            new PlayerInvUpdate(),
            new SyncHealthTypes(),
            new ActionBarSystem()
    };

    private EntityManager() { entities = new HashMap<>(); }

    public static void init() { if (INSTANCE.isNone()) INSTANCE = Option.some(new EntityManager()); }

    public void addEntity(CEntity entity) {
        if (containsEntity(entity.id)) return;
        entities.put(entity.id, entity);
    }
    public void removeEntity(UUID id) {
        entities.remove(id);
    }

    public Option<CEntity> getEntity(UUID id) {
        if (!entities.containsKey(id)) return Option.none();
        return Option.some(entities.get(id));
    }

    public void update(CrumbMMO plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (EntitySystem system : SYSTEMS) {
                    system.callCount += 1;
                    if (system.tickFreq != system.callCount) continue;

                    List<EntityComponent.ComponentType> query = system.getQuery();
                    Stream<CEntity> matches = entities.values()
                            .stream()
                            .filter(entity -> entity.hasComponents(query));
                    system.execute(matches);
                    system.callCount = 0;
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public boolean containsEntity(UUID id) { return entities.containsKey(id); }

}




























