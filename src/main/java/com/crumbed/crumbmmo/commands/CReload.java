package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.managers.ItemManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class CReload extends BrigadierCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("creload")
            .requires(s -> s.getBukkitSender().hasPermission("cmmo.admin"))
            .executes(c -> {
                ItemManager.reload();
                return Command.SINGLE_SUCCESS;
            });
    }
}
