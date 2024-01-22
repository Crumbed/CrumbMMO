package com.crumbed.crumbmmo.commands;


import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public abstract class BrigadierCommand {

    public abstract LiteralArgumentBuilder<CommandSourceStack> build();

    public LiteralArgumentBuilder<CommandSourceStack> literal(@NotNull String s) {
        return LiteralArgumentBuilder.literal(s);
    }

    public <T> RequiredArgumentBuilder<CommandSourceStack, T> arg(@NotNull String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public <T> T getArg(CommandContext<CommandSourceStack> c, String name, Class<T> clazz, T defaultReturn) {
        try {
            return c.getArgument(name, clazz);
        } catch (IllegalArgumentException ignored) {
            return defaultReturn;
        }
    }


}






















































