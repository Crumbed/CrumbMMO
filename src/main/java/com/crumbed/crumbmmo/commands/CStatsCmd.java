package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.stats.GenericStat;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

@CommandInfo(name = "cstats", requiresPlayer = false, treeCommand = true)
public class CStatsCmd extends CustomCommand {
    public static TabComponent[][] ARGS = new TabComponent[][] {
            { new TabComponent(TabComponent.Type.PlayerName, Option.some(PlayerManager.INSTANCE), true) },
    };

    @Override
    public void execute(CommandSender sender, String[] args) {
        CPlayer player;
        if (args.length >= 1) {
            String ign = args[0];
            Option<CPlayer> optPlayer = PlayerManager
                    .INSTANCE
                    .getPlayer(Bukkit.getPlayer(ign));

            if (optPlayer.isNone()) {
                sender.sendMessage(ChatColor.RED + "Cannot find player: " + ign); return; }
            player = optPlayer.unwrap();
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to view your stats.");
            return;
        } else {
            Player rawPlayer = (Player) sender;
            Option<CPlayer> optPlayer = PlayerManager
                    .INSTANCE
                    .getPlayer(rawPlayer);

            if (optPlayer.isNone()) {
                rawPlayer.sendMessage(ChatColor.RED + "Somehow you don't exist, please report this."); return; }
            player = optPlayer.unwrap();
        }

        sender.sendMessage(player.displayStats());
    }


    public static SubCommand SET = new SubCommand("set", "cmmo.admin", false, new TabComponent[][] {
            {
                    new TabComponent(TabComponent.Type.Id, Option.some(GenericStat.Damage), false),
                    new TabComponent(TabComponent.Type.Int, Option.none(), false),
                    new TabComponent(TabComponent.Type.PlayerName, Option.some(PlayerManager.INSTANCE), false)
            }
    }, (sender, args) -> {
        if (args.length != 3) { sender
                .sendMessage(ChatColor.RED + "Syntax error: expected 3 args but found " + args.length + ", do /cadmin for help."); return; }

        boolean maxFlag = false;
        if (args[0].substring(0, 4).equalsIgnoreCase("max_")) {
            maxFlag = true;
            args[0] = args[0].substring(4);
        }
        Option<GenericStat> optGenStat = GenericStat.fromString(args[0]);
        if (optGenStat.isNone()) { sender.sendMessage(ChatColor.RED + "Syntax error: invalid stat-id \"" + args[0] + "\""); return; }
        GenericStat genericStat = optGenStat.unwrap();

        double value;
        try {
            value = Double.parseDouble(args[1]);
        } catch(NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Syntax error: invalid value \"" + args[1] + "\", value must be a number.");
            return;
        }

        Option<CPlayer> optPlayer = PlayerManager
                .INSTANCE
                .getPlayer(Bukkit.getPlayer(args[2]));

        if (optPlayer.isNone()) {
            sender.sendMessage(ChatColor.RED + "Player error: could not find player with name " + args[2] + ", do /list to see all players"); return; }
        CPlayer player = optPlayer.unwrap();

        if (maxFlag) player.getStats().setMaxFromGeneric(genericStat, value);
        else player.getStats().setFromGeneric(genericStat, value);
    });



    public static SubCommand RESET = new SubCommand("reset", "cmmo.admin", false, new TabComponent[][]{
            {
                    new TabComponent(TabComponent.Type.Id, Option.some(GenericStat.Damage), false),
                    new TabComponent(TabComponent.Type.PlayerName, Option.some(PlayerManager.INSTANCE), false)
            }
    }, (sender, args) -> {
        if (args.length != 2) { sender
                .sendMessage(ChatColor.RED + "Syntax error: expected 2 args but found " + args.length + ", do /cadmin for help."); return; }

        Option<CPlayer> optPlayer = PlayerManager
                .INSTANCE
                .getPlayer(Bukkit.getPlayer(args[1]));

        if (optPlayer.isNone()) {
            sender.sendMessage(ChatColor.RED + "Player error: could not find player with name " + args[1] + ", do /list to see all players"); return; }
        CPlayer player = optPlayer.unwrap();


        if (args[0].equalsIgnoreCase("all")) {
            Stream.of(GenericStat.values())
                    .forEach(s -> player.getStats().resetFromGeneric(s));

            player.inv.hasUpdated = true;
            return;
        }
        if (args[0].substring(0, 4).equalsIgnoreCase("max_")) {
            args[0] = args[0].substring(4);
        }

        Option<GenericStat> optGenStat = GenericStat.fromString(args[0]);
        if (optGenStat.isNone()) { sender.sendMessage(ChatColor.RED + "Syntax error: invalid stat-id \"" + args[0] + "\""); return; }
        GenericStat genericStat = optGenStat.unwrap();

        player.getStats().resetFromGeneric(genericStat);
    });
}


























