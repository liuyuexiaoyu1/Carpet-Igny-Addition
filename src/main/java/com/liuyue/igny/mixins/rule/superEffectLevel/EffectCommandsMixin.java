package com.liuyue.igny.mixins.rule.superEffectLevel;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.EffectCommands;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EffectCommands.class)
public class EffectCommandsMixin {
    @WrapMethod(method = "register")
    private static void integer(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Operation<Void> original) {
        try {
            IGNYSettings.effectCommandRegistering.set(true);
            original.call(dispatcher, context);
        } finally {
            IGNYSettings.effectCommandRegistering.set(false);
        }
    }
}
