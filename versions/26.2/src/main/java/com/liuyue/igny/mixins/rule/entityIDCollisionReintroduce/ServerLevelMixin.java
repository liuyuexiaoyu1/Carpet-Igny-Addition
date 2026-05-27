package com.liuyue.igny.mixins.rule.entityIDCollisionReintroduce;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @WrapOperation(method = "getNextEntityId", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;hasEntityWithId(I)Z"))
    private boolean hasEntityWithId(ServerChunkCache instance, int i, Operation<Boolean> original) {
        return IGNYSettings.ENTITY_ID_COLLISION_REINTRODUCE.value() || original.call(instance, i);
    }
}
