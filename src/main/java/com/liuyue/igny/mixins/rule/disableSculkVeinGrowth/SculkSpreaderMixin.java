package com.liuyue.igny.mixins.rule.disableSculkVeinGrowth;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.SculkSpreader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SculkSpreader.class)
public class SculkSpreaderMixin {
    @ModifyVariable(
            method = "updateCursors(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;Z)V",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private boolean disableConversion(boolean shouldConvertBlocks) {
        return !IGNYSettings.DISABLE_SCULK_VEIN_GROWTH.value() && shouldConvertBlocks;
    }
}
