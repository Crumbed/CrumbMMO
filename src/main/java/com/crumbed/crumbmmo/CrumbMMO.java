package com.crumbed.crumbmmo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.crumbed.crumbmmo.commands.BrigadierCommand;
import com.crumbed.crumbmmo.ecs.components.*;
import com.crumbed.crumbmmo.ecs.systems.*;
import com.crumbed.crumbmmo.genericEvents.*;
import com.crumbed.crumbmmo.items.components.ItemLore;
import com.crumbed.crumbmmo.items.components.ItemStats;
import com.crumbed.crumbmmo.managers.*;
import com.crumbed.crumbmmo.jsonUtils.MobData;
import com.crumbed.crumbmmo.utils.Namespaces;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UUID;

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
        CraftingManager.INSTANCE = CraftingManager.init(this);
        StatManager.init();
        ItemManager.INSTANCE = new ItemManager.Builder(this)
                .with(ItemLore.class)
                .with(ItemStats.class)
                .create();

        EntityManager.INSTANCE = new EntityManager.Builder(this)
                .withComponent(EntityActionBar.class)
                .withComponent(EntityInventory.class)
                .withComponent(EntityStats.class)
                .withComponent(RawEntity.class)
                .withComponent(EntityName.class)
                .withComponent(NameTag.class)
                .withComponent(HealthTag.class)
                .withComponent(NpcComponent.class)
                .withSystem(new ActionBarSystem())
                .withSystem(new PlayerInvUpdate())
                .withSystem(new StatRegen())
                .withSystem(new SyncHealthTypes())
                .withSystem(new EntityGC())
                .withSystem(new SyncHealthTag())
                .create();
        PlayerManager.init(this);
        NpcManager.INSTANCE = NpcManager.loadNpcs(this);
        MobData.loadMobData(this);


        pm.registerEvents(new PlayerJoinAndLeave(this), this);
        pm.registerEvents(new PlayerInvUpdate(), this);
        pm.registerEvents(new MobSpawn(), this);
        pm.registerEvents(new ChunkLoad(), this);
        pm.registerEvents(new EntityDamage(), this);
        pm.registerEvents(new PlayerRespawn(), this);
        pm.registerEvents(new CraftingListener(), this);
        pm.registerEvents(new NpcEvents(), this);

        getProtocol().addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.WORLD_PARTICLES
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                var packet = event.getPacket();
                var packetId = packet.getIntegers().read(0);
                if (packetId == 24 || packetId == 0) event.setCancelled(true);
            }
        });

        var server = ((CraftServer) getServer()).getServer();
        var dispatcher = server.resources.managers().commands.getDispatcher();

        // Gaming for auto register commands
        Reflections classes = new Reflections("com.crumbed.crumbmmo.commands");
        for (Class<?> clazz : classes.get(SubTypes.of(BrigadierCommand.class).asClass())) {
            try {
                var cmd = (BrigadierCommand) clazz.getDeclaredConstructor().newInstance();
                dispatcher.register(cmd.build());
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
        NpcManager.INSTANCE.saveNpcs(this);
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

