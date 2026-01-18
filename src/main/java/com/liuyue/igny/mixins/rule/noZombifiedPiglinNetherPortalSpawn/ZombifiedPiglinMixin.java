package com.liuyue.igny.mixins.rule.noZombifiedPiglinNetherPortalSpawn;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public class ZombifiedPiglinMixin {
    @Inject(method="randomTick", at=@At("HEAD"), cancellable = true)
    private void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        if (IGNYSettings.noZombifiedPiglinNetherPortalSpawn) {
            ci.cancel();
        }
    }
}