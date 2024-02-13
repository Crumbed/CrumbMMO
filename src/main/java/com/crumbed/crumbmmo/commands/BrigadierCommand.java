package com.crumbed.crumbmmo.commands;


import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public abstract class BrigadierCommand {

    public abstract LiteralArgumentBuilder<CommandSourceStack> build();

    public LiteralArgumentBuilder<CommandSourceStack> literal(@NotNull String s) {
        return LiteralArgumentBuilder.literal(s);
    }

    public <T> RequiredArgumentBuilder<CommandSourceStack, T> arg(@NotNull String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public RequiredArgumentBuilder<CommandSourceStack, String> arg(Params param) {
        return RequiredArgumentBuilder.argument(param.name(), string());
    }

    public static <T> T getArg(CommandContext<CommandSourceStack> c, String name, Class<T> clazz, T defaultReturn) {
        try {
            return c.getArgument(name, clazz);
        } catch (IllegalArgumentException ignored) {
            return defaultReturn;
        }
    }



    public enum Params {
        Player ("player-name"),
        Npc ("npc-id"),
        Stat ("stat");



        private final String raw;

        Params(String s) { raw = s; }

        public RequiredArgumentBuilder<CommandSourceStack, String> arg() {
            return RequiredArgumentBuilder.argument(raw, string());
        }

        @Override
        public String toString() { return raw; }
    }
}






















































