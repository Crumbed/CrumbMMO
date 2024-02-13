package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.ecs.CEntity;
import com.crumbed.crumbmmo.ecs.NPC;
import com.crumbed.crumbmmo.managers.EntityManager;
import com.crumbed.crumbmmo.managers.NpcManager;
import com.crumbed.crumbmmo.managers.PlayerManager;
import com.crumbed.crumbmmo.utils.Option;
import com.crumbed.crumbmmo.utils.Some;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.luckperms.api.event.util.Param;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Npc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftLocation;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos;

public class NpcCmd extends BrigadierCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("npc")
            .requires(s -> s.getBukkitSender().hasPermission("cmmo.admin"))
            .then(Params.Npc.arg()
                .suggests(NpcManager::suggest)
                .executes(NpcCmd::execute)
            )
            .then(literal("create")
                .then(Params.Npc.arg()
                    .then(arg("xzy", blockPos())
                        .executes(NpcCmd::executeCreate)
                    )
                )
            )
            .then(literal("set")
                .then(literal("pos")
                    .then(arg("xzy", blockPos())
                        .then(Params.Npc.arg()
                            .suggests(NpcManager::suggest)
                            .executes(c -> {
                                final var blockPos = c.getArgument("xzy", BlockPos.class);
                                final var id = c.getArgument(Params.Npc.name(), String.class);
                                final var loc = new Location(c.getSource().getLevel().getWorld(),
                                    blockPos.getX(),
                                    blockPos.getY(),
                                    blockPos.getZ()
                                );

                                c.getSource().getBukkitSender()
                                    .sendMessage(NpcManager.INSTANCE.setLocation(id, loc));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )

                .then(literal("name")
                    .then(arg("npc-name", string())
                        .then(Params.Npc.arg()
                            .suggests(NpcManager::suggest)
                            .executes(c -> {
                                final var name = c.getArgument("npc-name", String.class);
                                final var id = c.getArgument(Params.Npc.name(), String.class);

                                c.getSource().getBukkitSender()
                                    .sendMessage(NpcManager.INSTANCE.setName(id, name));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )

                .then(literal("id")
                    .then(arg("new-id", string())
                        .then(Params.Player.arg()
                            .suggests(NpcManager::suggest)
                            .executes(c -> {
                                final var newId = c.getArgument("new-id", String.class);
                                final var id = c.getArgument(Params.Npc.name(), String.class);

                                c.getSource().getBukkitSender()
                                    .sendMessage(NpcManager.INSTANCE.setId(id, newId));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )

                .then(literal("skin")
                    .then(Params.Player.arg()
                        .suggests(PlayerManager::suggest)
                        .then(Params.Npc.arg()
                            .suggests(NpcManager::suggest)
                            .executes(c -> {
                                final var playerName = c.getArgument(Params.Player.name(), String.class);
                                final var id = c.getArgument(Params.Npc.name(), String.class);

                                c.getSource().getBukkitSender()
                                    .sendMessage(NpcManager.INSTANCE.setSkin(id, playerName));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
            )
            .then(literal("flag")
                .then(arg("flag-type", string())
                    .then(arg("flag", bool())
                        .suggests((c, builder) -> {
                            builder.suggest("true");
                            builder.suggest("false");
                            return builder.buildFuture();
                        })
                        .then(Params.Npc.arg()
                            .suggests(NpcManager::suggest)
                            .executes(c -> {
                                final var flagType = c.getArgument("flag-type", String.class);
                                final var flag = c.getArgument("flag", Boolean.class);
                                final var id = c.getArgument(Params.Npc.name(), String.class);

                                c.getSource().getBukkitSender()
                                    .sendMessage(NpcManager.INSTANCE.setFlag(id, flagType, flag));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
            )
            .executes(c -> {
                c.getSource().getBukkitSender()
                    .sendMessage(ChatColor.RED + "Syntax error: required 1 arg but found 0, \"/npc <npc-id>\"");
                return Command.SINGLE_SUCCESS;
            });
    }


    public static int execute(CommandContext<CommandSourceStack> c) {
        final var id = c.getArgument(Params.Npc.name(), String.class);
        var npcs = NpcManager.INSTANCE;
        if (!npcs.npcs.containsKey(id)) {
            c.getSource().getBukkitSender()
                .sendMessage(ChatColor.RED + "ERROR: No npc with name, " + id + ", exists!");
            return Command.SINGLE_SUCCESS;
        }

        var optNpc = EntityManager.INSTANCE.getEntity(npcs.npcs.get(id));
        if (!(optNpc instanceof Some<CEntity> s)) {
            c.getSource().getBukkitSender()
                .sendMessage(ChatColor.RED + "Something has gone horribly wrong and the entity doesnt exist! contact Crumbs if you see this.");
            return Command.SINGLE_SUCCESS;
        }
        var npc = (NPC) s.inner();

        var gson = new GsonBuilder().setPrettyPrinting().create();
        c.getSource().getBukkitSender().sendMessage(ChatColor.GREEN + gson.toJson(npc));
        return Command.SINGLE_SUCCESS;
    }

    public static int executeCreate(CommandContext<CommandSourceStack> c) {
        final var blockPos = c.getArgument("xyz", BlockPos.class);
        final var id = c.getArgument(Params.Npc.name(), String.class);
        final var loc = new Location(c.getSource().getLevel().getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());

        NpcManager.INSTANCE.createNpc(id, loc);
        c.getSource().getBukkitSender()
            .sendMessage(ChatColor.GREEN + "Successfully created npc at: " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
        return Command.SINGLE_SUCCESS;
    }

}





















