package com.liuyue.igny.mixins.easterEgg;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.StringUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.time.LocalDate;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @WrapOperation(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;logChatMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType$Bound;Ljava/lang/String;)V"))
    private void logChatMessage(MinecraftServer instance, Component content, ChatType.Bound boundChatType, String header, Operation<Void> original) {
        LocalDate date = LocalDate.now();
        if (IGNYSettings.FESTIVE_EASTER_EGG.value() && date.getMonthValue() == 4 && date.getDayOfMonth() == 1) {
            original.call(instance, Component.literal(StringUtil.reverse(content.getString())), boundChatType, header);
            return;
        }
        original.call(instance, content, boundChatType, header);
    }
}
