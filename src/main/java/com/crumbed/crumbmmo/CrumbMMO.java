package com.crumbed.crumbmmo;

import com.crumbed.crumbmmo.commands.CustomCommand;
import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.serializable.MobData;
import com.crumbed.crumbmmo.ecs.components.EntityActionBar;
import com.crumbed.crumbmmo.ecs.components.EntityInventory;
import com.crumbed.crumbmmo.ecs.components.EntityStats;
import com.crumbed.crumbmmo.ecs.components.RawEntity;
import com.crumbed.crumbmmo.ecs.systems.ActionBarSystem;
import com.crumbed.crumbmmo.ecs.systems.StatRegen;
import com.crumbed.crumbmmo.ecs.systems.SyncHealthTypes;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.ecs.systems.PlayerInvUpdate;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.genericEvents.PlayerJoinAndLeave;
import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.managers.StatManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static org.reflections.scanners.Scanners.SubTypes;


public final class CrumbMMO extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager pm = this.getServer().getPluginManager();


        StatManager.init();
        ItemManager.init(this);
        EntityManager.INSTANCE = new EntityManager.Builder()
                .withComponent(EntityActionBar.class)
                .withComponent(EntityInventory.class)
                .withComponent(EntityStats.class)
                .withComponent(RawEntity.class)
                .withSystem(new ActionBarSystem())
                .withSystem(new PlayerInvUpdate())
                .withSystem(new StatRegen())
                .withSystem(new SyncHealthTypes())
                .create();
        PlayerManager.init(this);
        MobData.loadMobData(this);


        pm.registerEvents(new PlayerJoinAndLeave(this), this);
        pm.registerEvents(new PlayerInvUpdate(), this);

        // Gaming for auto register commands
        Reflections classes = new Reflections("com.crumbed.crumbmmo.commands");
        for (Class<?> clazz : classes.get(SubTypes.of(CustomCommand.class).asClass())) {
            try {
                CustomCommand customCommand = (CustomCommand) clazz.getDeclaredConstructor().newInstance();
                getCommand(customCommand.getCommandInfo().name()).setExecutor(customCommand);
                getCommand(customCommand.getCommandInfo().name()).setTabCompleter(customCommand);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        EntityManager
                .INSTANCE
                .update(this);
        getLogger().info("CMMO loaded");
    }

    @Override
    public void onDisable() {
        PlayerManager.INSTANCE.unwrap().writeData(this);
        // Plugin shutdown logic
    }
}
