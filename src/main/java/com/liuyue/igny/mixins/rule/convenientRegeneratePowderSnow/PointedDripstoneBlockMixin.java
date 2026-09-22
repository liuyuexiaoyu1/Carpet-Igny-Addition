package com.liuyue.igny.mixins.rule.convenientRegeneratePowderSnow;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {
    //#if >= 26.1
    /*$$@WrapOperation(method = "lambda$getFluidAboveStalactite$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean is(BlockState instance, Object block, Operation<Boolean> original) {$$*/
    //#else
    @WrapOperation(method = "method_33279", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean is(BlockState instance, Block block, Operation<Boolean> original) {
        //#endif
        if (IGNYSettings.CONVENIENT_REGENERATE_POWDER_SNOW.value()) {
            return original.call(instance, block) || instance.is(Blocks.POWDER_SNOW);
        }
        return original.call(instance, block);
    }

    //#case
    //? >= 26.1 ? @WrapOperation(method = "lambda$spawnDripParticle$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)V"))
    //? >= 1.21.11 ? @WrapOperation(method = "method_33280", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)V"))
    @WrapOperation(method = "method_33280", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)V")) //?else
    //#endcase
    private static void spawnDripParticle(Level level, BlockPos pos, BlockState state, Fluid fluid, Operation<Void> original, @Local(argsOnly = true) PointedDripstoneBlock.FluidInfo optional) //#replace >= 1.21.11 ? private static void spawnDripParticle(Level level, BlockPos pos, BlockState state, Fluid fluid, BlockPos pos2, Operation<Void> original, @Local(argsOnly = true) PointedDripstoneBlock.FluidInfo optional)
    {
        if (IGNYSettings.CONVENIENT_REGENERATE_POWDER_SNOW.value() && optional.sourceState().is(Blocks.POWDER_SNOW)) {
            original.call(level, pos, optional.sourceState(), fluid); //#replace >= 1.21.11 ? original.call(level, pos, optional.sourceState(), fluid, pos2);
            return;
        }
        original.call(level, pos, state, fluid); //#replace >= 1.21.11 ? original.call(level, pos, state, fluid, pos2);
    }

    @WrapOperation(method = "spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")) //#replace >= 1.21.11 ? @WrapOperation(method = "spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private static void addParticle(Level instance, ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Operation<Void> original, @Local(argsOnly = true) BlockState blockState) {
        if (IGNYSettings.CONVENIENT_REGENERATE_POWDER_SNOW.value() && blockState.is(Blocks.POWDER_SNOW)) {
            original.call(instance, ParticleTypes.SNOWFLAKE, x, y, z, xSpeed, ySpeed, zSpeed);
            return;
        }
        original.call(instance, particle, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
