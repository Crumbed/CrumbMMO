package com.crumbed.crumbmmo.genericEvents;


import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.ecs.NPC;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.utils.None;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataType;

import static com.crumbed.crumbmmo.utils.Namespaces.*;


public class ChunkLoad implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity) || entity instanceof Player) continue;
            var data = entity.getPersistentDataContainer();
            var session = data.get(SESSION, PersistentDataType.STRING);
            if (CrumbMMO.getSessionId().toString().equals(session)) {
                var id = switch (data.get(MOB_ID, PersistentDataType.INTEGER)) {
                    case null -> -1;
                    case Integer i -> (int) i;
                };
                var cEnt = EntityManager
                        .INSTANCE
                        .getEntity(id);
                if (cEnt.isSome() && switch (cEnt.unwrap().getComponent(RawEntity.class)) {
                    case Some<RawEntity> s -> s.inner().id.equals(entity.getUniqueId());
                    case None<RawEntity> ignored -> false;
                }) return;
            }
            var level = (data.has(MOB_LEVEL, PersistentDataType.INTEGER))
                    ? data.get(MOB_LEVEL, PersistentDataType.INTEGER)
                    : EntityManager.getMobData().getBaseLevel(entity.getType())
                    + (int) (Math.random() * 4 + 0.5);
            Option<String> name = (data.has(MOB_NAME, PersistentDataType.STRING))
                    ? Option.some(data.get(MOB_NAME, PersistentDataType.STRING))
                    : Option.none();

            EntityManager.getMobData()
                    .generateMob((LivingEntity) entity, level, name);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity) || entity instanceof Player) continue;
            var data = entity.getPersistentDataContainer();
            var session = data.get(SESSION, PersistentDataType.STRING);
            if (!CrumbMMO.getSessionId().toString().equals(session)) continue;
            var id = switch (data.get(MOB_ID, PersistentDataType.INTEGER)) {
                case null -> -1;
                case Integer i -> (int) i;
            };
            var cEnt = EntityManager
                    .INSTANCE
                    .getEntity(id);
            if (cEnt.isSome() && cEnt.unwrap() instanceof NPC) continue;
            if (cEnt.isSome() && switch (cEnt.unwrap().getComponent(RawEntity.class)) {
                case Some<RawEntity> s -> s.inner().id.equals(entity.getUniqueId());
                case None<RawEntity> ignored -> false;
            }) EntityManager.INSTANCE.killEntity(id);
        }
    }
}














