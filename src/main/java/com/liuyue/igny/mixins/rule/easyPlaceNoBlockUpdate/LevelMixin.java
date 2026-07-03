package com.liuyue.igny.mixins.rule.easyPlaceNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Level.class)
public class LevelMixin {
    @ModifyVariable(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(value = "HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private int igny_stripNeighborUpdates(int flags) {
        if (!"false".equals(IGNYSettings.EASY_PLACE_NO_BLOCK_UPDATE.value()) && IGNYSettings.easyPlaceProtocolActive.get()) {
            return 2 | 16;
        }
        return flags;
    }
}
