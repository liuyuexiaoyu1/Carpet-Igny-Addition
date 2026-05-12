package com.liuyue.igny.mixins.rule.stickyResinBlock;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin {
    @Shadow
    private static boolean isSticky(BlockState state) {
        return false;
    }

    @Inject(method = "isSticky", at = @At(value = "HEAD"), cancellable = true)
    private static void isSticky(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.stickyResinBlock) {
            if (state.is(Blocks.RESIN_BLOCK)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "canStickToEachOther", at = @At(value = "HEAD"), cancellable = true)
    private static void canStickToEachOther(BlockState state1, BlockState state2, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.stickyResinBlock) {
            if (state1.is(Blocks.HONEY_BLOCK) && state2.is(Blocks.SLIME_BLOCK)) {
                cir.setReturnValue(false);
                return;
            }
            if (state1.is(Blocks.SLIME_BLOCK) && state2.is(Blocks.HONEY_BLOCK)) {
                cir.setReturnValue(false);
                return;
            }
            if (state1.is(Blocks.RESIN_BLOCK) && (state2.is(Blocks.SLIME_BLOCK) || state2.is(Blocks.HONEY_BLOCK))) {
                cir.setReturnValue(false);
                return;
            }
            if (state2.is(Blocks.RESIN_BLOCK) && (state1.is(Blocks.SLIME_BLOCK) || state1.is(Blocks.HONEY_BLOCK))) {
                cir.setReturnValue(false);
                return;
            }
            cir.setReturnValue(isSticky(state1) || isSticky(state2));
        }
    }
}
