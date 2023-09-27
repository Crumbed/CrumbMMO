package com.crumbed.crumbmmo.genericEvents;


import com.crumbed.crumbmmo.CrumbMMO;
import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.crumbed.crumbmmo.utils.Namespaces.*;


public class ChunkLoad implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            PersistentDataContainer data = entity.getPersistentDataContainer();
            String session = data.get(SESSION, PersistentDataType.STRING);
            if (CrumbMMO.getSessionId().toString().equals(session)) {
                int id = data.get(MOB_ID, PersistentDataType.INTEGER);
                Option<CEntity> cEnt = EntityManager
                        .INSTANCE
                        .getEntity(id);
                if (cEnt.isSome()
                        && cEnt.unwrap().hasComponent(RawEntity.class)
                        && cEnt.unwrap().getComponent(RawEntity.class)
                        .unwrap()
                        .id
                        .equals(entity.getUniqueId())
                ) return;
            }
            int level = (data.has(MOB_LEVEL, PersistentDataType.INTEGER))
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
}














