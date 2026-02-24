package com.liuyue.igny.mixins.rule.removeSyncmaticaPermission;

//#if MC < 26.1
import carpet.utils.Translations;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.MessageType;
import ch.endte.syncmatica.communication.ServerCommunicationManager;
//#if MC <= 12004
//$$ import ch.endte.syncmatica.ServerPlacement;
//#else
import ch.endte.syncmatica.data.ServerPlacement;
//#endif
//#if MC > 12004
import ch.endte.syncmatica.network.PacketType;
//#endif
//#endif
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.CommandUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.FriendlyByteBuf;
//#if MC >= 26.1
//$$ import com.liuyue.igny.utils.compat.DummyClass;
//#endif
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC <= 12004
//$$ import net.minecraft.resources.ResourceLocation;
//#endif

import java.util.Map;

//#if MC >= 26.1
//$$ @Mixin(DummyClass.class)
//#else
@Mixin(ServerCommunicationManager.class)
//#endif
@Pseudo
public abstract class ServerCommunicationManagerMixin {
    //#if MC < 26.1
    @Shadow
    @Final
    private Map<ExchangeTarget, ServerPlayer> playerMap;

    @Shadow
    public abstract void sendMessage(ExchangeTarget client, MessageType type, String identifier);

    //#if MC <= 12004
    //$$  @Inject(method = "handle", at = @At(value = "INVOKE", target = "Lch/endte/syncmatica/communication/ServerCommunicationManager;getModifier(Lch/endte/syncmatica/ServerPlacement;)Lch/endte/syncmatica/communication/exchange/Exchange;"), require = 0, cancellable = true)
    //$$  private void handlePacket(ExchangeTarget source, ResourceLocation id, FriendlyByteBuf packetBuf, CallbackInfo ci, @Local(name = "placement") ServerPlacement placement) {
    //#else
    @Inject(method = "handle", at = @At(value = "INVOKE", target = "Lch/endte/syncmatica/communication/ServerCommunicationManager;getModifier(Lch/endte/syncmatica/data/ServerPlacement;)Lch/endte/syncmatica/communication/exchange/Exchange;"), require = 0, cancellable = true)
    private void handlePacket(ExchangeTarget source, PacketType type, FriendlyByteBuf packetBuf, CallbackInfo ci, @Local(name = "placement") ServerPlacement placement) {
        //#endif
        ServerPlayer player = this.playerMap.get(source);
        if (!CommandUtils.canUseCommand(player, IGNYSettings.removeSyncmaticaPermission) && placement.getOwner().uuid != player.getUUID()) {
            if (source != null) {
                this.sendMessage(source, MessageType.ERROR, Translations.tr("igny.syncmatica_not_permission"));
            }
            ci.cancel();
        }
    }
    //#endif
}
