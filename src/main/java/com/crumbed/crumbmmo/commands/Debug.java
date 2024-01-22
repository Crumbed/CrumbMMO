package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.displays.CDisplay;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.managers.NpcManager;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.jsonUtils.PlayerData;
import com.crumbed.crumbmmo.utils.Option;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@CommandInfo(name = "cdebug", permission = "cmmo.debug", requiresPlayer = false, treeCommand = true)
public class Debug extends CustomCommand {
    public static TabComponent[][] ARGS = {
            { new TabComponent(TabComponent.Type.Lit, Option.some(new Literal("")), false) }
    };


    @Override
    public void execute(CommandSender sender, String[] args) {
    }


    public static SubCommand PLAYERS = new SubCommand("players", "", false, new TabComponent[0][0], (sender, args) -> {
        sender.sendMessage("Collecting players...");
        Stream<PlayerData> players = PlayerManager
                .INSTANCE
                .getPlayers()
                .filter(Objects::nonNull)
                .map(CPlayer::asData);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        players.forEach(p -> sender.sendMessage(gson.toJson(p)));
    });

    public static SubCommand ENTITIES = new SubCommand("entities", "", false, new TabComponent[0][0], (sender, args) -> {
        var i = new AtomicInteger();
        EntityManager.INSTANCE
                .getEntities()
                .map(x -> {
                    var rep = String.format("%s : %s", i, (x == null)
                            ? "NULL"
                            : "Entity"
                    );
                    i.addAndGet(1);
                    return rep;
                })
                .forEach(sender::sendMessage);
    });


    public static SubCommand GET_ENTITY = new SubCommand("get_entity", "", false, new TabComponent[][] {
            { new TabComponent(TabComponent.Type.Id, Option.some(EntityManager.INSTANCE), false) }
    }, (sender, args) -> {
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Error: " + args[0] + " is an invalid <id>.");
            return;
        }

        var e = EntityManager
                .INSTANCE
                .getEntity(id);

        if (e.isNone()) {
            sender.sendMessage(ChatColor.RED + "Error: there is not entity with id " + id);
            return;
        }

        var gson = new GsonBuilder().setPrettyPrinting().create();
        sender.sendMessage(String.format(
                "%s%s",
                ChatColor.GRAY,
                gson.toJson(e.unwrap())
        ));
    });


    public static SubCommand NPC_TEST = new SubCommand("npctest", "", true, new TabComponent[][] {
    }, (sender, args) -> {
        Bukkit.getLogger().info("testing npc");
        NpcManager.INSTANCE.createNpc("test", ((Player) sender).getLocation());
    });

    public static SubCommand NPC_2ND_LAYER = new SubCommand("npc2ndlayer", "", true, new TabComponent[][] {
    }, (sender, args) -> {
        var p = (Player) sender;
        NpcManager.INSTANCE.getNpcs().forEach(npc -> {
            var con = ((CraftPlayer) p).getHandle().connection;
            con.send(new ClientboundSetEntityDataPacket(npc.raw.getId(), npc.raw.getEntityData().packDirty()));
        });
    });

    public static SubCommand DISPLAY_TEST = new SubCommand("newdisplay", "", true, new TabComponent[][] {
    }, (sender, args) -> {
        var p = (Player) sender;
        var display = new CDisplay(p.getWorld(), 16, 9);
        display.setLocation(p.getEyeLocation().getX(), p.getEyeLocation().getY(), p.getEyeLocation().getZ());
    });

    public static SubCommand KILL_DISPLAY = new SubCommand("killdisplay", "", true, new TabComponent[][] {
            { new TabComponent(TabComponent.Type.Id, Option.some(new CDisplay()), true) }
    }, (sender, args) -> {
        var p = (Player) sender;
        var display = CDisplay.displays.get(UUID.fromString(args[0]));
        for (var row : display.rows) {
            row.raw.remove();
        }

        CDisplay.displays.remove(UUID.fromString(args[0]));
    });
}





















