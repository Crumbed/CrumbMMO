package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.stats.GenericStat;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class CStatsCmd extends BrigadierCommand {



    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("cstats")
            .requires(s -> true)
            .then(Params.Player.arg()
                .suggests(PlayerManager::suggest)
                .executes(CmmoCmd::executeStats)
            )
            .then(literal("set")
                .requires(s -> s.getBukkitSender().hasPermission("cmmo.admin"))
                .then(Params.Stat.arg()
                    .suggests(GenericStat::suggest)
                    .then(arg("value", doubleArg())
                        .then(Params.Player.arg()
                            .suggests(PlayerManager::suggest)
                            .executes(CStatsCmd::executeSet)
                        ).executes(CStatsCmd::executeSet)
                    ).executes(CStatsCmd::setFail)
                ).executes(CStatsCmd::setFail)
            )
            .then(literal("reset")
                .requires(s -> s.getBukkitSender().hasPermission("cmmo.admin"))
                .then(Params.Stat.arg()
                    .suggests(GenericStat::suggest)
                    .then(Params.Player.arg()
                        .suggests(PlayerManager::suggest)
                        .executes(CStatsCmd::executeReset)
                    ).executes(CStatsCmd::executeReset)
                )
                .then(Params.Player.arg()
                    .suggests(PlayerManager::suggest)
                    .executes(CStatsCmd::executeReset)
                ).executes(CStatsCmd::executeReset)
            )
            .executes(CmmoCmd::executeStats);
    }


    public static int setFail(CommandContext<CommandSourceStack> c) {
        c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Invalid usage! /cstats set <stat> <value> <player>");
        return Command.SINGLE_SUCCESS;
    }
    public static int executeSet(CommandContext<CommandSourceStack> c) {
        var stat = c.getArgument(Params.Stat+"", String.class);
        var value = c.getArgument("value", Double.class);
        var playerName = c.getArgument(Params.Player+"", String.class);
        var optPlayer = switch (playerName) {
            case null -> (c.getSource().getBukkitSender() instanceof Player p)
                ? PlayerManager.INSTANCE.getPlayer(p)
                : Option.<CPlayer>none();
            case String name -> PlayerManager.INSTANCE.getPlayer(name);
        };

        if (!(optPlayer instanceof Some<CPlayer> somePlayer)) {
            c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Could not find player");
            return Command.SINGLE_SUCCESS;
        }
        var p = somePlayer.inner();

        var maxFlag = false;
        if (stat.substring(0, 4).equalsIgnoreCase("max-")) {
            maxFlag = true;
            stat = stat.substring(4);
        }

        var optGenStat = GenericStat.fromString(stat);
        if (optGenStat.isNone()) {
            c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Syntax error: invalid stat-id \"" + stat + "\"");
            return Command.SINGLE_SUCCESS;
        }
        var genericStat = optGenStat.unwrap();

        if (maxFlag) p.getStats().setMaxFromGeneric(genericStat, value);
        else p.getStats().setFromGeneric(genericStat, value);

        return Command.SINGLE_SUCCESS;
    }



    public static int executeReset(CommandContext<CommandSourceStack> c) {
        var stat = c.getArgument(Params.Stat+"", String.class);
        var playerName = c.getArgument(Params.Player+"", String.class);
        var optPlayer = switch (playerName) {
            case null -> (c.getSource().getBukkitSender() instanceof Player p)
                ? PlayerManager.INSTANCE.getPlayer(p)
                : Option.<CPlayer>none();
            case String name -> PlayerManager.INSTANCE.getPlayer(name);
        };

        if (!(optPlayer instanceof Some<CPlayer> somePlayer)) {
            c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Could not find player");
            return Command.SINGLE_SUCCESS;
        }
        var p = somePlayer.inner();


        if (stat == null) {
            Stream.of(GenericStat.values()).forEach(s -> p.getStats().resetFromGeneric(s));

            p.inv.hasUpdated = true;
            return Command.SINGLE_SUCCESS;
        }
        else if (stat.substring(0, 4).equalsIgnoreCase("max-")) {
            stat = stat.substring(4);
        }

        var optGenStat = GenericStat.fromString(stat);
        if (optGenStat.isNone()) {
            c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Syntax error: invalid stat-id \"" + stat + "\"");
            return Command.SINGLE_SUCCESS;
        }
        var genericStat = optGenStat.unwrap();

        p.getStats().resetFromGeneric(genericStat);
        return Command.SINGLE_SUCCESS;
    }

}


























