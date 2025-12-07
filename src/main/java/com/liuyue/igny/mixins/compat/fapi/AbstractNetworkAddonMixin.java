package com.liuyue.igny.mixins.compat.fapi;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.patches.FakeClientConnection;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.mixins.compat.accessor.fapi.AbstractChanneledNetworkAddonAccessor;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import net.fabricmc.fabric.impl.networking.GlobalReceiverRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = AbstractNetworkAddon.class, priority = 999, remap = false)
public abstract class AbstractNetworkAddonMixin {
    @SuppressWarnings("RedundantIfStatement")
    @WrapWithCondition(method = "lateInit", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/networking/GlobalReceiverRegistry;startSession(Lnet/fabricmc/fabric/impl/networking/AbstractNetworkAddon;)V"))
    private boolean notStartSession_ifFakeClientConnection(GlobalReceiverRegistry<?> instance, AbstractNetworkAddon<?> addon) {
        if ((Boolean.TRUE.equals(getCarpetOrgAdditionSetting())||IGNYSettings.fakePlayerSpawnMemoryLeakFix) && addon instanceof AbstractChanneledNetworkAddon<?>) {
            Connection connection = ((AbstractChanneledNetworkAddonAccessor) addon).getConnection();
            if (connection instanceof FakeClientConnection) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private static Boolean getCarpetOrgAdditionSetting() {
            if(FabricLoader.getInstance().getAllMods().stream().map(ModContainer::getMetadata).map(ModMetadata::getId).anyMatch(id -> id.contains("carpet") && id.contains("org") && id.contains("addition"))){
                CarpetRule<?> carpetRule = CarpetServer.settingsManager.getCarpetRule("fakePlayerSpawnMemoryLeakFix");
                if (carpetRule == null) {
                    return false;
                }
                return carpetRule.value() instanceof Boolean value ? value :false;
            }
            return false;
    }
}
