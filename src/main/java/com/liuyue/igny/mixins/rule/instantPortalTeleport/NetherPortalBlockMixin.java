package com.liuyue.igny.mixins.rule.instantPortalTeleport;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
//#if MC >= 26.2
//$$ import net.minecraft.world.entity.EntityTypes;
//#else
import net.minecraft.world.entity.EntityType;
//#endif
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {
    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;)Lnet/minecraft/world/entity/Entity;"), cancellable = true)
    private void spawn(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (IGNYSettings.INSTANT_PORTAL_TELEPORT.value()) {
            //#if MC >= 26.2
            //$$ Entity entity = EntityTypes.ZOMBIFIED_PIGLIN.create(level,
            //#else
            Entity entity = EntityType.ZOMBIFIED_PIGLIN.create(level,
            //#endif
                    //#if MC <= 12004
                    //$$ null,
                    //#endif
                    null,
                    pos.above(), MobSpawnType.STRUCTURE, false, false);
            if (entity != null) {
                entity.setPortalCooldown();
                ci.cancel();
            }
        }
    }
}
