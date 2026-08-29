package com.liuyue.igny.mixins.rule.betterPointedDripstoneGrow;

//#if MC < 26.1
import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 26.1
//$$ @Mixin(DummyClass.class)
//#else
@Mixin(PointedDripstoneBlock.class)
//#endif
public class PointedDripstoneBlockMixin {
    //#if MC < 26.1
    @Inject(method = "canGrow", at = @At(value = "HEAD"), cancellable = true)
    private static void canGrow(BlockState dripstoneState, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.BETTER_POINTED_DRIPSTONE_GROW.value()) {
            FluidState fluidState = state.getFluidState();
            cir.setReturnValue(dripstoneState.is(Blocks.DRIPSTONE_BLOCK) && fluidState.is(Fluids.WATER) && fluidState.isSource());
        }
    }
    //#endif
}
