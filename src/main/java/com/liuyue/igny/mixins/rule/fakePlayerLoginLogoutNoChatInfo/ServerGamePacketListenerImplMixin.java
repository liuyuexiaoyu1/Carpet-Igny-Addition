package com.liuyue.igny.mixins.rule.fakePlayerLoginLogoutNoChatInfo;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.task.ITask;
import com.liuyue.igny.task.TaskManager;
import com.liuyue.igny.task.vault.VaultTask;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = ServerGamePacketListenerImpl.class,priority = 1500)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @WrapOperation(
            //#if MC <= 12001
            //$$ method="onDisconnect",
            //#else
            method="removePlayerFromWorld",
            //#endif
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V")
    )
    private void disableLogoutMessage(PlayerList instance, Component component, boolean bl, Operation<Void> original) {
        if (this.player instanceof EntityPlayerMPFake) {
            //#if MC >= 12003
            String playerName = this.player.getName().getString();

            boolean isVaultFake = false;
            for (ITask task : TaskManager.getAllActiveTasks()) {
                if (!(task instanceof VaultTask vaultTask)) continue;

                if (playerName.equals(vaultTask.getLogoutPlayerName())) {
                    isVaultFake = true;
                    break;
                }

                ServerPlayer current = vaultTask.getCurrentFakePlayer();
                if (current != null && current == this.player) {
                    isVaultFake = true;
                    break;
                }
            }
            //#endif
            if (
                //#if MC >= 12003
                    isVaultFake ||
                 //#endif
            IGNYSettings.fakePlayerLoginLogoutNoChatInfo) {
                return;
            }
        }
        original.call(instance, component, bl);
    }
}
