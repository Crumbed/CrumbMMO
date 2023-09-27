package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.stats.GenericStat;
import com.crumbed.crumbmmo.utils.Option;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

@CommandInfo(name = "cmmo", requiresPlayer = false, treeCommand = true)
public class CmmoCmd extends CustomCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        // todo help command
    }

    @SubCommand(name = { "stats" }, params = { "<player-name>" }, requiresPlayer = false)
    public void stats(CommandSender sender, String[] args) {
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

    @SubCommand(name = { "stats", "set" }, params = {
            "strength <value> <player-ign>",
            "crit_damage <value> <player-ign>",
            "crit_chance <value> <player-ign>",
            "health <value> <player-ign>",
            "max_health <value> <player-ign>",
            "defense <value> <player-ign>",
            "mana <value> <player-ign>",
            "max_mana <value> <player-ign>",
            "damage <value> <player-ign>"
    }, requiresPlayer = false, permission = "cmmo.admin")
    public void setStat(CommandSender sender, String[] args) {
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
    }

    @SubCommand(name = { "stats", "reset" }, params = {
            "strength <player-ign>",
            "crit_damage <player-ign>",
            "crit_chance <player-ign>",
            "health <player-ign>",
            "defense <player-ign>",
            "mana <player-ign>",
            "damage <player-ign>",
            "all <player-ign>"
    }, requiresPlayer = false, permission = "cmmo.admin")
    public void resetStat(CommandSender sender, String[] args) {
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
        Option<GenericStat> optGenStat = GenericStat.fromString(args[0]);
        if (optGenStat.isNone()) { sender.sendMessage(ChatColor.RED + "Syntax error: invalid stat-id \"" + args[0] + "\""); return; }
        GenericStat genericStat = optGenStat.unwrap();

        player.getStats().resetFromGeneric(genericStat);
    }
}














