package com.liuyue.igny.mixins.compat.fapi;

import carpet.patches.FakeClientConnection;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.mixins.compat.accessor.fapi.AbstractChanneledNetworkAddonAccessor;
import com.liuyue.igny.utils.RuleUtils;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = AbstractNetworkAddon.class, remap = false)
public abstract class AbstractNetworkAddonMixin {
    //#if MC <= 12002
    @Shadow
    protected abstract void invokeInitEvent();

    @Inject(method = "lateInit",at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/networking/GlobalReceiverRegistry;startSession(Lnet/fabricmc/fabric/impl/networking/AbstractNetworkAddon;)V"), cancellable = true)
    private void notEndSession_ifFakeClientConnection2(CallbackInfo ci) {
        AbstractNetworkAddon<?> addon = (AbstractNetworkAddon<?>) (Object) this;
        if ((Boolean.TRUE.equals(RuleUtils.getCarpetRulesValue("carpet-org-addition", "fakePlayerSpawnMemoryLeakFix")) || IGNYSettings.fakePlayerSpawnMemoryLeakFix) && addon instanceof AbstractChanneledNetworkAddon<?>) {
            Connection connection = ((AbstractChanneledNetworkAddonAccessor) addon).getConnection();
            if (connection instanceof FakeClientConnection) {
                invokeInitEvent();
                ci.cancel();
            }
        }
    }
    //#endif
}
