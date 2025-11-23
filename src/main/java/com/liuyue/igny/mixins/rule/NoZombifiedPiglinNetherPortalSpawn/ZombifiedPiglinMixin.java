package com.liuyue.igny.mixins.rule.NoZombifiedPiglinNetherPortalSpawn;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombifiedPiglin.class)
public class ZombifiedPiglinMixin {

    @Inject(
            method = "checkZombifiedPiglinSpawnRules",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void preventSpawningInOrAbovePortal(
            net.minecraft.world.entity.EntityType<ZombifiedPiglin> entityType,
            LevelAccessor level,
            net.minecraft.world.entity.MobSpawnType spawnType,
            BlockPos pos,
            net.minecraft.util.RandomSource random,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (IGNYSettings.NoZombifiedPiglinNetherPortalSpawn) {
            if (level.getBlockState(pos).is(Blocks.NETHER_PORTAL) ||
                    level.getBlockState(pos.above()).is(Blocks.NETHER_PORTAL)) {
                cir.setReturnValue(false);
            }
        }
    }
}