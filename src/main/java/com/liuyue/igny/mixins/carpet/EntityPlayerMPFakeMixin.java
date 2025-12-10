package com.liuyue.igny.mixins.carpet;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.task.ITask;
import com.liuyue.igny.task.TaskManager;
import com.liuyue.igny.task.vault.VaultTask;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.players.GameProfileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(EntityPlayerMPFake.class)
public abstract class EntityPlayerMPFakeMixin {

    @WrapOperation(
            method = "createFake",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/players/GameProfileCache;get(Ljava/lang/String;)Ljava/util/Optional;"
            )
    )
    private static Optional<GameProfile> getFakeProfile(GameProfileCache instance, String string, Operation<Optional<GameProfile>> original, @Local(argsOnly = true, name = "arg0") String username) {
        Optional<GameProfile> gameProfile = original.call(instance, string);
        if(gameProfile.isEmpty()) {
            for (ITask task : TaskManager.getAllActiveTasks()) {
                if (task instanceof VaultTask vaultTask && username.equals(vaultTask.getPendingFakeName())) {
                    return Optional.of(new GameProfile(UUIDUtil.createOfflinePlayerUUID(username), username));
                }
            }
        }
        return gameProfile;
    }
}