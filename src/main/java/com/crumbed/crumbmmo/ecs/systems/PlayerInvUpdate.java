package com.crumbed.crumbmmo.ecs.systems;

import com.crumbed.crumbmmo.ecs.*;
import com.crumbed.crumbmmo.ecs.components.EntityInventory;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryView;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerInvUpdate extends EntitySystem implements Listener {
    public PlayerInvUpdate() {
        super(5, new ComponentQuery(EntityStats.class, RawEntity.class, EntityInventory.class));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        update(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        update(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) { update(e.getEntity().getUniqueId()); }

    private void update(UUID playerUuid) {
        Option<CPlayer> p = PlayerManager
                .INSTANCE
                .getPlayer(playerUuid);
        if (p.isNone()) return;

        p.unwrap().inv.hasUpdated = true;
    }

    public static final int DAMAGE = 0;
    public static final int STRENGTH = 1;
    public static final int CRITDAMAGE = 2;
    public static final int CRITCHANCE = 3;
    public static final int HEALTH = 4;
    public static final int DEFENSE = 5;
    public static final int MANA = 6;
    public static final int HEALTH_REGEN = 7;
    public static final int MANA_REGEN = 8;

    @Override
    public void execute(Stream<ComponentQuery.Result> results) {
        results.forEach(r -> {
            var entity = r
                    .getComponent(RawEntity.class)
                    .unwrap()
                    .getLivingEntity(r.parentEntity);
            if (entity.isNone() || !(entity.unwrap() instanceof Player player)) return;

            EntityStats stats = r
                    .getComponent(EntityStats.class)
                    .unwrap();
            EntityInventory inv = r
                    .getComponent(EntityInventory.class)
                    .unwrap();



            if (inv.activeSlot != player.getInventory().getHeldItemSlot())
                inv.hasUpdated = true;

            if (inv.hasUpdated) {
                double[] swapStats = PlayerManager
                        .INSTANCE
                        .syncPlayerInv(r.parentEntity);

                inv.statBoosts.addAll(Arrays.asList(inv.armor));
                inv.statBoosts.add(inv.inventory[inv.activeSlot]);
                for (CItem item : inv.statBoosts) {
                    swapStats[DAMAGE] += item.getStat("damage").value;
                    swapStats[STRENGTH] += item.getStat("strength").value;
                    swapStats[CRITDAMAGE] += item.getStat("crit-damage").value;
                    swapStats[CRITCHANCE] += item.getStat("crit-chance").value;
                    swapStats[HEALTH] += item.getStat("health").value;
                    swapStats[DEFENSE] += item.getStat("defense").value;
                    swapStats[MANA] += item.getStat("mana").value;
                }

                stats.damage.value += swapStats[DAMAGE];
                stats.strength.value += swapStats[STRENGTH];
                stats.critDamage.value += swapStats[CRITDAMAGE];
                stats.critChance.value += swapStats[CRITCHANCE];
                stats.health.max.value += swapStats[HEALTH];
                stats.defense.value += swapStats[DEFENSE];
                stats.mana.max.value += swapStats[MANA];
                inv.hasUpdated = false;
                inv.statBoosts.clear();
            }
        });
    }
}







































