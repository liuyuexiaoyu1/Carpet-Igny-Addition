package com.liuyue.igny.mixins.rule.preventWeakLoadingEntityPiling;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "tickServer", at = @At(value = "RETURN"))
    private void igny$preventWeakLoadingEntityPiling(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        if (!IGNYSettings.PREVENT_WEAK_LOADING_ENTITY_PILING.value()) {
            return;
        }
        if (server.getTickCount() % 20 != 0) {
            return;
        }
        for (ServerLevel level : server.getAllLevels()) {
            cleanWeakLoadedProjectiles(level);
        }
    }

    @Unique
    private void cleanWeakLoadedProjectiles(ServerLevel level) {
        List<Entity> projectiles = new ArrayList<>(level.getEntities(EntityTypeTest.forClass(WitherSkull.class), e -> true));
        projectiles.addAll(level.getEntities(EntityTypeTest.forClass(ShulkerBullet.class), e -> true));
        if (projectiles.isEmpty()) {
            return;
        }

        for (Entity entity : projectiles) {
            if (entity.isRemoved()) {
                continue;
            }
            BlockPos pos = entity.blockPosition();
            if (level.isPositionEntityTicking(pos)) {
                continue;
            }
            entity.discard();
        }
    }
}
