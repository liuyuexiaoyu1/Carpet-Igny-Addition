package com.liuyue.igny.mixins.rule.windChargeNoBlockStateChange;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;
@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {

    @Inject(method = "onExplosionHit", at = @At("HEAD"), cancellable = true)
    private void igny$windChargeNoBlockStateChange(ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> consumer, CallbackInfo ci) {
        if (IGNYSettings.WIND_CHARGE_NO_BLOCK_STATE_CHANGE.value()
                && explosion.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK) {
            ci.cancel();
        }
    }
}
