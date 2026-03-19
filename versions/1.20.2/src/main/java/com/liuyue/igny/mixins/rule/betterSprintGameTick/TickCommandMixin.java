package com.liuyue.igny.mixins.rule.betterSprintGameTick;

import carpet.commands.TickCommand;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TickCommand.class)
public class TickCommandMixin {
    @Inject(method = "setTps", at = @At(value = "RETURN"))
    private static void setTps(CommandSourceStack source, float tps, CallbackInfoReturnable<Integer> cir) {
        IGNYSettings.originalTPS = tps;
    }
}
