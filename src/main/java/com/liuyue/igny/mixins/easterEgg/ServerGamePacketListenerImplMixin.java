package com.liuyue.igny.mixins.easterEgg;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.FestivalUtil;
import com.liuyue.igny.utils.StringUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.chat.PlayerChatMessage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @WrapOperation(
            //#if MC >= 26.1
            //$$ method = "lambda$handleChat$1",
            //#else
            method = "method_45064",
            //#endif
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V"
            )
    )
    private void wrapBroadcastChatMessage(
            ServerGamePacketListenerImpl instance,
            PlayerChatMessage message,
            Operation<Void> original
    ) {
        if (IGNYSettings.FESTIVE_EASTER_EGG.value() && FestivalUtil.isAprilFoolsDay()) {
            String originalText = message.signedContent();
            if (!originalText.startsWith("/")) {
                String reversed = StringUtil.reverse(originalText);
                message = message.withUnsignedContent(Component.literal(reversed));
            }
        }
        original.call(instance, message);
    }
}