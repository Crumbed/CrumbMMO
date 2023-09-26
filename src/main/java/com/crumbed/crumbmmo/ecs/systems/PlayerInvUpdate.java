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

    private void update(UUID playerUuid) {
        Option<CPlayer> p = PlayerManager
                .INSTANCE
                .unwrap()
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

    @Override
    public void execute(Stream<ComponentQuery.Result> results) {
        results.forEach(r -> {
            Option<LivingEntity> entity = r
                    .getComponent(RawEntity.class)
                    .unwrap()
                    .getLivingEntity();
            if (entity.isNone() || !(entity.unwrap() instanceof Player)) return;
            Player player = (Player) entity.unwrap();

            EntityStats stats = r
                    .getComponent(EntityStats.class)
                    .unwrap();
            EntityInventory inv = r
                    .getComponent(EntityInventory.class)
                    .unwrap();



            CItem activeItem = inv.inventory[player.getInventory().getHeldItemSlot()];
            if (activeItem != null && !activeItem.equals(inv.inventory[inv.activeSlot]))
                inv.hasUpdated = true;

            if (inv.hasUpdated) {
                double[] swapStats = PlayerManager
                        .INSTANCE
                        .unwrap()
                        .syncPlayerInv(r.parentEntity);

                inv.statBoosts.addAll(Arrays.asList(inv.armor));
                inv.statBoosts.add(inv.inventory[inv.activeSlot]);
                for (CItem item : inv.statBoosts) {
                    swapStats[DAMAGE] += item.getStat("damage").getValue();
                    swapStats[STRENGTH] += item.getStat("strength").getValue();
                    swapStats[CRITDAMAGE] += item.getStat("crit-damage").getValue();
                    swapStats[CRITCHANCE] += item.getStat("crit-chance").getValue();
                    swapStats[HEALTH] += item.getStat("health").getValue();
                    swapStats[DEFENSE] += item.getStat("defense").getValue();
                    swapStats[MANA] += item.getStat("mana").getValue();
                }

                stats.damage.setValue(stats.damage.getValue() + swapStats[DAMAGE]);
                stats.strength.setValue(stats.strength.getValue() + swapStats[STRENGTH]);
                stats.critDamage.setValue(stats.critDamage.getValue() + swapStats[CRITDAMAGE]);
                stats.critChance.setValue(stats.critChance.getValue() + swapStats[CRITCHANCE]);
                stats.health.setBaseValue(stats.health.getBaseValue() + swapStats[HEALTH]);
                stats.defense.setValue(stats.defense.getValue() + swapStats[DEFENSE]);
                stats.mana.setBaseValue(stats.mana.getBaseValue() + swapStats[MANA]);
                inv.hasUpdated = false;
                inv.statBoosts.clear();
            }
        });
    }
}







































