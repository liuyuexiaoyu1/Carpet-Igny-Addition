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
//#if MC>=12105
//$$ import net.minecraft.world.entity.InsideBlockEffectApplier;
//#endif


@Mixin(WitherRoseBlock.class)
public class WitherRoseBlockMixin {
    @Inject(method="entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), cancellable = true)
    private void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity
            //#if MC>=12105
            //$$ , InsideBlockEffectApplier insideBlockEffectApplier
            //#endif
            //#if MC>=12109
            //$$ , boolean bl
            //#endif
            , CallbackInfo ci) {
        if (IGNYSettings.noWitherEffect) {
            ci.cancel();
        }
    }
}
