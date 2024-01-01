package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.NPC;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.managers.NpcManager;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "npc", permission = "cmmo.admin", treeCommand = true, requiresPlayer = false)
public class NpcCmd extends CustomCommand {
    public static TabComponent[][] ARGS = {
            { new TabComponent(TabComponent.Type.Id, Option.some(NpcManager.INSTANCE), false) }
    };


    @Override
    public void execute(CommandSender sender, String[] args) {
        var id = args[0];
        var npcs = NpcManager.INSTANCE;
        if (!npcs.npcs.containsKey(id)) {
            sender.sendMessage(ChatColor.RED + "ERROR: No npc with name, " + id + ", exists!");
            return;
        }

        var optNpc = EntityManager.INSTANCE.getEntity(npcs.npcs.get(id));
        if (!(optNpc instanceof Some<CEntity> s)) {
            sender.sendMessage(ChatColor.RED + "Something has gone horribly wrong and the entity doesnt exist! contact Crumbs if you see this.");
            return;
        }
        var npc = (NPC) s.inner();

        var gson = new GsonBuilder().setPrettyPrinting().create();
        sender.sendMessage(ChatColor.GREEN + gson.toJson(npc));
    }



    public static SubCommand CREATE = new SubCommand("create", "cmmo.admin", false, new TabComponent[][] {
            {
                    new TabComponent(TabComponent.Type.Id, Option.none(), false),
                    new TabComponent(TabComponent.Type.Number, Option.none(), false),
                    new TabComponent(TabComponent.Type.Number, Option.none(), false),
                    new TabComponent(TabComponent.Type.Number, Option.none(), false)
            }
    }, (sender, args) -> {

        Location loc;
        try {
            loc = new Location(
                    (sender instanceof Player p) ? p.getWorld() : Bukkit.getWorld("world"),
                    Double.parseDouble(args[1]),
                    Double.parseDouble(args[2]),
                    Double.parseDouble(args[3])
            );
        } catch (NumberFormatException ignored) {
            sender.sendMessage(ChatColor.RED + "Parse error: failed to parse", args[1], args[2], args[3], "as a float");
            return;
        }

        NpcManager.INSTANCE.createNpc(args[0], loc);
        sender.sendMessage(ChatColor.GREEN + "Successfully created npc at: " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
    });

    public static SubCommand SET = new SubCommand("set", "cmmo.admin", false, new TabComponent[][] {
            {
                    new TabComponent(TabComponent.Type.Lit, Option.some(new Literal(
                            "name",
                            "id",
                            "skin")),false
                    ),
                    new TabComponent(TabComponent.Type.Id, Option.some(new Literal("<name>")), false),
                    new TabComponent(TabComponent.Type.Id, Option.some(NpcManager.INSTANCE), false)
            },
            {
                    new TabComponent(TabComponent.Type.Lit, Option.some(new Literal(
                            "pos",
                            "position")), false
                    ),
                    new TabComponent(TabComponent.Type.Number, Option.none(), false),
                    new TabComponent(TabComponent.Type.Number, Option.none(), false),
                    new TabComponent(TabComponent.Type.Number, Option.none(), false),
                    new TabComponent(TabComponent.Type.Id, Option.some(NpcManager.INSTANCE), false)
            },
            {
                    new TabComponent(TabComponent.Type.Lit, Option.some(new Literal(
                            "always_look")), false),
                    new TabComponent(TabComponent.Type.Lit, Option.some(new Literal(
                            "true",
                            "false")), false),
                    new TabComponent(TabComponent.Type.Id, Option.some(NpcManager.INSTANCE), false)
            }
    }, (sender, args) -> {
        var manager = NpcManager.INSTANCE;

        switch (args[0]) {
            case "name" -> {
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Syntax error: expected 3 arguments but found " + args.length);
                    break;
                }
                if (!manager.setName(args[2], args[1])) {
                    sender.sendMessage(ChatColor.RED + "Error: could not find NPC with id " + args[2]);
                    break;
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully changed npc name to " + args[1]);
            }

            case "id" -> {
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Syntax error: expected 3 arguments but found " + args.length);
                    break;
                }
                if (!manager.setId(args[2], args[1])) {
                    sender.sendMessage(ChatColor.RED + "Error: either id is already in use or the npc specified does not exist.");
                    break;
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully changed npc id to " + args[1]);
            }

            case "always_look" -> {
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Error: Syntax error: expected 3 arguments but found " + args.length);
                    break;
                }

                boolean flag = Boolean.parseBoolean(args[1]);
                if (!manager.setFlag(args[2], args[0], flag)) {
                    sender.sendMessage(ChatColor.RED + "Error: could not find NPC with id " + args[2]);
                    break;
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully set NPC to always look at player");
            }

            case "skin" -> {
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Error: Syntax error: expected 3 arguments but found " + args.length);
                    break;
                }

                if (!manager.setSkin(args[2], args[1])) {
                    sender.sendMessage(ChatColor.RED + "Error: could not find NPC with id " + args[2] + ", or player name was invalid");
                    break;
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully set NPC skin to that of " + args[1]);
            }

            case "pos", "position" -> {
                Location loc;
                try {
                    loc = new Location(
                            (sender instanceof Player p) ? p.getWorld() : Bukkit.getWorld("world"),
                            Double.parseDouble(args[1]),
                            Double.parseDouble(args[2]),
                            Double.parseDouble(args[3])
                    );
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ChatColor.RED + "Parse error: failed to parse", args[1], args[2], args[3], "as a float");
                    break;
                }

                if (!manager.setLocation(args[4], loc)) {
                    sender.sendMessage(ChatColor.RED + "Error: could not find NPC with id " + args[4]);
                    break;
                }

                sender.sendMessage(ChatColor.GREEN + "Successfully moved npc to " + loc);
            }
        }
    });
}























