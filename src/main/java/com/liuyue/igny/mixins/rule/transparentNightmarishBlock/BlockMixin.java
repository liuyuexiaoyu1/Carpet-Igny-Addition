package com.liuyue.igny.mixins.rule.transparentNightmarishBlock;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtil;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "getExplosionResistance", at = @At("HEAD"), cancellable = true)
    private void getExplosionResistance(CallbackInfoReturnable<Float> cir) {
        if (IGNYSettings.TRANSPARENT_NIGHTMARISH_BLOCK.value()) {
            Block block = (Block) (Object) this;
            if (RuleUtil.isNightmarishBlock(block)) {
                cir.setReturnValue(1200f);
            }
        }
    }
}
