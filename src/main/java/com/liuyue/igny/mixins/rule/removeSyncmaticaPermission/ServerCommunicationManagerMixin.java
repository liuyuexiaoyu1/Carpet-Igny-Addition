package com.liuyue.igny.mixins.rule.removeSyncmaticaPermission;

import carpet.utils.Translations;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.MessageType;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
import ch.endte.syncmatica.network.PacketType;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.CommandUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerCommunicationManager.class)
@Pseudo
public abstract class ServerCommunicationManagerMixin {
    @Shadow
    @Final
    private Map<ExchangeTarget, ServerPlayer> playerMap;

    @Shadow
    public abstract void sendMessage(ExchangeTarget client, MessageType type, String identifier);

    @Inject(method = "handle", at = @At(value = "INVOKE", target = "Lch/endte/syncmatica/data/SyncmaticManager;getPlacement(Ljava/util/UUID;)Lch/endte/syncmatica/data/ServerPlacement;"), require = 0, cancellable = true)
    private void handlePacket(ExchangeTarget source, PacketType type, FriendlyByteBuf packetBuf, CallbackInfo ci) {
        if (!CommandUtils.canUseCommand(this.playerMap.get(source), IGNYSettings.removeSyncmaticaPermission)) {
            if (source != null) {
                this.sendMessage(source, MessageType.ERROR, Translations.tr("igny.syncmatica_not_permission"));
            }
            ci.cancel();
        }
    }
}
