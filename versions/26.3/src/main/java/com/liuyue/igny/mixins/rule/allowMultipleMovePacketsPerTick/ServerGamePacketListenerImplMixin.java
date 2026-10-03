package com.liuyue.igny.mixins.rule.allowMultipleMovePacketsPerTick;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @WrapOperation(method = "handleMovePlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;receivedPositionThisTick:Z", opcode = Opcodes.GETFIELD))
    private boolean receivedPositionThisTick(ServerGamePacketListenerImpl instance, Operation<Boolean> original) {
        return !IGNYSettings.ALLOW_MULTIPLE_MOVE_PACKETS_PER_TICK.value() && original.call(instance);
    }
}
