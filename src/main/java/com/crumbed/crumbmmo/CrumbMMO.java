package com.crumbed.crumbmmo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.crumbed.crumbmmo.commands.CustomCommand;
import com.crumbed.crumbmmo.ecs.components.*;
import com.crumbed.crumbmmo.ecs.systems.*;
import com.crumbed.crumbmmo.genericEvents.*;
import com.crumbed.crumbmmo.managers.*;
import com.crumbed.crumbmmo.serializable.MobData;
import com.crumbed.crumbmmo.utils.Namespaces;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.C;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UUID;

import static com.crumbed.crumbmmo.utils.Namespaces.MOB_ID;
import static org.reflections.scanners.Scanners.SubTypes;


public final class CrumbMMO extends JavaPlugin {
    private static UUID SESSION_ID;
    private static ProtocolManager PROTOCOL_MANAGER;
    private static CrumbMMO INSTANCE;

    @Override
    public void onEnable() {
        SESSION_ID = UUID.randomUUID();
        PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();
        INSTANCE = this;
        saveDefaultConfig();
        PluginManager pm = this.getServer().getPluginManager();
        Namespaces.initNamespaces(this);

        TimerManager.INSTANCE = TimerManager.init(this);
        StatManager.init();
        ItemManager.init(this);
        EntityManager.INSTANCE = new EntityManager.Builder(this)
                .withComponent(EntityActionBar.class)
                .withComponent(EntityInventory.class)
                .withComponent(EntityStats.class)
                .withComponent(RawEntity.class)
                .withComponent(EntityName.class)
                .withComponent(NameTag.class)
                .withComponent(HealthTag.class)
                .withSystem(new ActionBarSystem())
                .withSystem(new PlayerInvUpdate())
                .withSystem(new StatRegen())
                .withSystem(new SyncHealthTypes())
                .withSystem(new EntityGC())
                .withSystem(new SyncHealthTag())
                .create();
        PlayerManager.init(this);
        MobData.loadMobData(this);


        pm.registerEvents(new PlayerJoinAndLeave(this), this);
        pm.registerEvents(new PlayerInvUpdate(), this);
        pm.registerEvents(new MobSpawn(), this);
        pm.registerEvents(new ChunkLoad(), this);
        pm.registerEvents(new EntityDamage(), this);
        pm.registerEvents(new PlayerRespawn(), this);

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
        EntityManager.INSTANCE
                .getEntities()
                .filter(Objects::nonNull)
                .map(e -> e.id)
                .forEach(id -> EntityManager.INSTANCE.killEntity(id));
        EntityManager.getMobData().saveMobData(this);
        // Plugin shutdown logic
    }



    public static UUID getSessionId() { return SESSION_ID; }
    public static ProtocolManager getProtocol() { return PROTOCOL_MANAGER; }
    public static CrumbMMO getInstance() { return INSTANCE; }
}

