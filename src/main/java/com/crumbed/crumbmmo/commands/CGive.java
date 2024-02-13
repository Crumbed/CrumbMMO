package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.ItemManager;
import com.crumbed.crumbmmo.items.CItem;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.utils.None;
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

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;


public class CGive extends BrigadierCommand {

    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("cgive")
            .requires(source -> source.getBukkitSender().hasPermission("cmmo.admin"))
            .then(arg("item-id", string())
                .suggests((c, builder) -> {
                    ItemManager
                        .INSTANCE
                        .getItemIds()
                        .filter(id -> id.startsWith(getArg(c, "item-id", String.class, "")))
                        .forEach(builder::suggest);

                    return builder.buildFuture();
                })
                .then(arg("count", integer())
                    .then(arg("player-name", string())
                        .suggests(PlayerManager::suggest)
                        .executes(c -> execute(
                            c,
                            c.getArgument("count", Integer.class),
                            Option.some(c.getArgument("player-name", String.class))
                        ))
                    ).executes(c -> execute(
                        c,
                        c.getArgument("count", Integer.class),
                        Option.none()
                    ))
                ).executes(c -> execute(c, 1, Option.none()))
            ).executes(c -> {
                c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Invalid usage! /cgive <item-id> <?count> <?player-name>");
                return Command.SINGLE_SUCCESS;
            });
    }

    public int execute(CommandContext<CommandSourceStack> c, int count, Option<String> playerName) {
        var itemId = c.getArgument("item-id", String.class);
        var item = ItemManager
            .INSTANCE
            .itemReg
            .get(c.getArgument("item-id", String.class));
        if (item == null) {
            c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Invalid item ID: " + itemId);
            return Command.SINGLE_SUCCESS;
        }
        var raw = item.getRawItem();
        raw.setAmount(count);

        var player = switch (playerName) {
            case Some<String> s -> Bukkit.getPlayer(s.inner());
            case None<String> ignored -> {
                if (c.getSource().getBukkitSender() instanceof Player p) yield p;
                playerName = Option.some("CONSOLE");
                yield null;
            }
        };

        if (player == null) {
            c.getSource().getBukkitSender().sendMessage(ChatColor.RED + "Error: could not find player \"" + playerName.unwrap() + "\"");
            return Command.SINGLE_SUCCESS;
        }

        player.getInventory().addItem(raw);
        c.getSource().getBukkitSender().sendMessage(ChatColor.GREEN + "Successfully gave " + count + " " + itemId + " to " + player.getDisplayName());
        return Command.SINGLE_SUCCESS;
    }

}




























