package com.liuyue.igny.mixins.rule.betterSprintGameTick;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TickCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TickCommand.class)
public class TickCommandMixin {
    @Inject(method = "setTickingRate", at = @At(value = "RETURN"))
    private static void onSetTickingRate(CommandSourceStack source, float tickRate, CallbackInfoReturnable<Integer> cir) {
        IGNYSettings.originalTPS = tickRate;
    }
}
