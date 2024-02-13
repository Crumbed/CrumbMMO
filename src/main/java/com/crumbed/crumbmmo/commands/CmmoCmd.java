package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.stats.GenericStat;
import com.crumbed.crumbmmo.stats.Stat;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class CmmoCmd extends BrigadierCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("cmmo")
            .requires(ignored -> true)
            .then(literal("stats")
                .then(arg("player-name", string())
                    .suggests(PlayerManager::suggest)
                    .executes(CmmoCmd::executeStats)
                ).executes(CmmoCmd::executeStats)
            ).executes(c -> {
                c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Invalid usage! /cmmo <[stats]>");
                return Command.SINGLE_SUCCESS;
            });
    }

    public static int executeStats(CommandContext<CommandSourceStack> c) {
        var playerName = c.getArgument("player-name", String.class);
        var optPlayer = switch(playerName) {
            case null -> (c.getSource().getBukkitSender() instanceof Player p)
                ? PlayerManager.INSTANCE.getPlayer(p)
                : Option.<CPlayer>none();
            case String name -> PlayerManager.INSTANCE.getPlayer(name);
        };

        if (!(optPlayer instanceof Some<CPlayer> p)) {
            c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Could not find player");
            return Command.SINGLE_SUCCESS;
        }
        c.getSource().getBukkitSender().sendMessage(p.inner().displayStats());
        return Command.SINGLE_SUCCESS;
    }

}














