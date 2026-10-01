package com.liuyue.igny.mixins.rule.spectatorCanOperateContainer;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @WrapOperation(method = "handleContainerClick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"
    ))
    private boolean containerClick(ServerPlayer instance, Operation<Boolean> original) {
        return igny$treatAsRegularPlayer(instance, original);
    }

    @WrapOperation(method = "handlePlaceRecipe", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"
    ))
    private boolean placeRecipe(ServerPlayer instance, Operation<Boolean> original) {
        return igny$treatAsRegularPlayer(instance, original);
    }

    @WrapOperation(method = "handleContainerButtonClick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"
    ))
    private boolean containerButtonClick(ServerPlayer instance, Operation<Boolean> original) {
        return igny$treatAsRegularPlayer(instance, original);
    }

    @WrapOperation(method = "handlePlayerAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"
    ))
    private boolean playerAction(ServerPlayer instance, Operation<Boolean> original) {
        return igny$treatAsRegularPlayer(instance, original);
    }

    @Unique
    private static boolean igny$treatAsRegularPlayer(ServerPlayer player, Operation<Boolean> original) {
        if (IGNYSettings.SPECTATOR_CAN_OPERATE_CONTAINER.value() && player.isSpectator()) {
            return false;
        }
        return original.call(player);
    }
}
