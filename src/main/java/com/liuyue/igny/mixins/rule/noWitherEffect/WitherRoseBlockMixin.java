package com.liuyue.igny.mixins.rule.noWitherEffect;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WitherRoseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?>= 1.21.5 ? import net.minecraft.world.entity.InsideBlockEffectApplier;


@Mixin(WitherRoseBlock.class)
public class WitherRoseBlockMixin {
    @Inject(method="entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), cancellable = true)
    private void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity
            //?>= 1.21.5 ? , InsideBlockEffectApplier insideBlockEffectApplier
            //?>= 1.21.9 ? , boolean bl
            , CallbackInfo ci) {
        if (IGNYSettings.NO_WITHER_EFFECT.value()) {
            ci.cancel();
        }
    }
}
