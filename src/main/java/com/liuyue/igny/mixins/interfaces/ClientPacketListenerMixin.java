package com.liuyue.igny.mixins.interfaces;

import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void connect(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        //#if MC >= 12006
        CustomItemMaxStackSizeDataManager.setClient(Minecraft.getInstance());
        //#endif
    }
}
