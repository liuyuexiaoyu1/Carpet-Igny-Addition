package com.liuyue.igny.mixins.rule.easyPlaceNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Unique
    private static final ThreadLocal<Boolean> igny_handling = ThreadLocal.withInitial(() -> false);

    @Unique
    private static boolean igny_isFromEasyPlace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 3; i < stack.length; i++) {
            String cls = stack[i].getClassName();
            if ("fi.dy.masa.litematica.util.EasyPlaceUtils".equals(cls) ||
                "fi.dy.masa.litematica.util.WorldUtils".equals(cls)) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (igny_handling.get()) return;
        if ("false".equals(IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value())) return;
        if (!igny_isFromEasyPlace()) return;

        BlockPos pos = hitResult.getBlockPos();
        if (hitResult.getLocation().x - (double) pos.getX() >= 2.0) return;

        Vec3 newLoc = new Vec3(pos.getX() + 2, hitResult.getLocation().y, hitResult.getLocation().z);
        BlockHitResult newHit = new BlockHitResult(newLoc, hitResult.getDirection(), pos, hitResult.isInside());

        igny_handling.set(true);
        try {
            if (Minecraft.getInstance().gameMode != null) {
                cir.setReturnValue(Minecraft.getInstance().gameMode.useItemOn(player, hand, newHit));
            }
        } finally {
            igny_handling.set(false);
        }
    }
}
