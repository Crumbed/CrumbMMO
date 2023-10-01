package com.crumbed.crumbmmo;

import com.crumbed.crumbmmo.commands.CustomCommand;
import com.crumbed.crumbmmo.ecs.components.*;
import com.crumbed.crumbmmo.ecs.systems.*;
import com.crumbed.crumbmmo.genericEvents.ChunkLoad;
import com.crumbed.crumbmmo.genericEvents.MobSpawn;
import com.crumbed.crumbmmo.serializable.MobData;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.genericEvents.PlayerJoinAndLeave;
import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.managers.StatManager;
import com.crumbed.crumbmmo.utils.Namespaces;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static org.reflections.scanners.Scanners.SubTypes;


public final class CrumbMMO extends JavaPlugin {
    private static UUID SESSION_ID;

    @Override
    public void onEnable() {
        SESSION_ID = UUID.randomUUID();
        saveDefaultConfig();
        PluginManager pm = this.getServer().getPluginManager();
        Namespaces.initNamespaces(this);

        StatManager.init();
        ItemManager.init(this);
        EntityManager.INSTANCE = new EntityManager.Builder(this)
                .withComponent(EntityActionBar.class)
                .withComponent(EntityInventory.class)
                .withComponent(EntityStats.class)
                .withComponent(RawEntity.class)
                .withComponent(EntityName.class)
                //.withComponent(NameTag.class)
                .withSystem(new ActionBarSystem())
                .withSystem(new PlayerInvUpdate())
                .withSystem(new StatRegen())
                .withSystem(new SyncHealthTypes())
                //.withSystem(new SyncNameTag())
                .create();
        PlayerManager.init(this);
        MobData.loadMobData(this);


        pm.registerEvents(new PlayerJoinAndLeave(this), this);
        pm.registerEvents(new PlayerInvUpdate(), this);
        pm.registerEvents(new MobSpawn(), this);
        pm.registerEvents(new ChunkLoad(), this);

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
        PlayerManager.INSTANCE.writeData(this);
        EntityManager.getMobData().saveMobData(this);
        // Plugin shutdown logic
    }



    public static UUID getSessionId() { return SESSION_ID; }
}

