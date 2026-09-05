/*
 * This file is part of the JoaCarpet project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Joa and contributors
 *
 * Ported to Carpet IGNY Addition (com.liuyue.igny) under LGPL-3.0.
 * Only the insaneBehaviors rule family is ported; all credits for the
 * original logic belong to the JoaCarpet authors.
 */

package com.liuyue.igny.commands;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.CommandUtil;
import com.liuyue.igny.utils.insaneBehaviors.InsaneBehaviors;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;

import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InsaneBehaviorsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("insanebehaviors")
                        .requires(c -> !Objects.equals(IGNYSettings.INSANE_BEHAVIORS.value(), "off") &&
                                CommandUtil.canUseCommand(c.getPlayer(), IGNYSettings.COMMAND_INSANE_BEHAVIORS.value()))

                        .executes(c -> 1)
                        .then(
                                literal("reset")
                                        .executes(c -> InsaneBehaviors.resetCounterAndResolution(c.getSource()))
                        )
                        .then(
                                literal("getstate")
                                        .executes(c -> InsaneBehaviors.getState(c.getSource()))
                        )
                        .then(
                                literal("setstate")
                                        .then(argument("resolution", IntegerArgumentType.integer(2))
                                                .then(argument("counter", IntegerArgumentType.integer(0))
                                                        .executes(c -> InsaneBehaviors.setState(c.getSource(),
                                                                IntegerArgumentType.getInteger(c, "resolution"),
                                                                IntegerArgumentType.getInteger(c, "counter")))))
                        )
        );
    }
}