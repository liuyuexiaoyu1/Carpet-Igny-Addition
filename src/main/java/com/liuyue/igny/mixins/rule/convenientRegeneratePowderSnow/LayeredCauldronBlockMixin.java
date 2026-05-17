package com.liuyue.igny.mixins.rule.convenientRegeneratePowderSnow;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if MC < 12003
//$$ import java.util.function.Predicate;
//#endif

@Mixin(LayeredCauldronBlock.class)
public class LayeredCauldronBlockMixin {
    //#if MC >= 12003
    @Shadow @Final private Biome.Precipitation precipitationType;
    //#else
    //$$ @Shadow @Final private Predicate<Biome.Precipitation> fillPredicate;
    //$$ @Shadow @Final public static Predicate<Biome.Precipitation> RAIN;
    //#endif

    @ModifyVariable(method = "receiveStalactiteDrip", at = @At(value = "HEAD"), argsOnly = true)
    private BlockState isFull(BlockState value, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        if (IGNYSettings.convenientRegeneratePowderSnow && value.is(Blocks.POWDER_SNOW)) {
            return level.getBlockState(pos);
        }
        return value;
    }

    @Inject(method = "canReceiveStalactiteDrip", at = @At(value = "RETURN"), cancellable = true)
    private void canReceiveStalactiteDrip(Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.convenientRegeneratePowderSnow && !cir.getReturnValueZ()) {
            //#if MC >= 12003
            cir.setReturnValue(fluid == Fluids.WATER && this.precipitationType == Biome.Precipitation.SNOW);
            //#else
            //$$ cir.setReturnValue(fluid == Fluids.WATER && this.fillPredicate == RAIN);
            //#endif
        }
    }
}
