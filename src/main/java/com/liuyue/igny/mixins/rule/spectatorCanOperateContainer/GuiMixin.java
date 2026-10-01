package com.liuyue.igny.mixins.rule.spectatorCanOperateContainer;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.SpectatorContainerTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui; //#replace >= 26.2 ? import net.minecraft.client.gui.Hud;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class) //#replace >= 26.2 ? @Mixin(Hud.class)
public class GuiMixin {
    @Inject(method = "canRenderCrosshairForSpectator", at = @At(value = "HEAD"), cancellable = true)
    private void igny$containerCrosshair(HitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYSettings.SPECTATOR_CAN_OPERATE_CONTAINER.value() || !(hitResult instanceof BlockHitResult blockHit)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (SpectatorContainerTarget.isContainerTarget(minecraft.level, blockHit.getBlockPos())) {
            cir.setReturnValue(true);
        }
    }
}