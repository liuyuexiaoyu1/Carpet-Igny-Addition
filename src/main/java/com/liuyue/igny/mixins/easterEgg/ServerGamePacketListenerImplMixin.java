package com.liuyue.igny.mixins.easterEgg;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.chat.PlayerChatMessage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.time.LocalDate;

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
        LocalDate date = LocalDate.now();
        if (IGNYSettings.festiveEasterEgg && date.getMonthValue() == 4 && date.getDayOfMonth() == 1) {
            String originalText = message.signedContent();
            if (!originalText.startsWith("/") && !originalText.startsWith("!")) {
                String reversed = reverse(originalText);
                message = message.withUnsignedContent(Component.literal(reversed));
            }
        }
        original.call(instance, message);
    }

    @Unique
    private String reverse(String msg) {
        int[] codePoints = msg.codePoints().toArray();
        StringBuilder sb = new StringBuilder();

        for (int i = codePoints.length - 1; i >= 0; i--) {
            sb.appendCodePoint(codePoints[i]);
        }

        return sb.toString();
    }
}