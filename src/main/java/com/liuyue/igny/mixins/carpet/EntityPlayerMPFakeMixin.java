package com.liuyue.igny.mixins.carpet;

import carpet.patches.EntityPlayerMPFake;
import com.liuyue.igny.task.ITask;
import com.liuyue.igny.task.TaskManager;
import com.liuyue.igny.task.vault.VaultTask;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.UUIDUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//?>= 1.21.9 ? import java.util.UUID;

@Mixin(EntityPlayerMPFake.class)
public abstract class EntityPlayerMPFakeMixin {
    //#if >= 1.20.3
    //#if >= 1.21.9
    /*$$@ModifyVariable(method = "createFake", at = @At("STORE"), name = "uuid")
    private static UUID modifyGameProfile(UUID value, @Local(argsOnly = true, name = "arg0") String username) {$$*/
    //#else
    @ModifyVariable(method = "createFake", at = @At("STORE"), name = "gameprofile")
    private static GameProfile modifyGameProfile(GameProfile value, @Local(argsOnly = true, name = "arg0") String username) {
    //#endif
        for (ITask task : TaskManager.getAllActiveTasks()) {
            if (task instanceof VaultTask vaultTask && username.equals(vaultTask.getPendingFakeName())) {
                return new GameProfile(UUIDUtil.createOfflinePlayerUUID(username), username); //#replace >= 1.21.9 ? return UUIDUtil.createOfflinePlayerUUID(username);
            }
        }
        return value;
    }
    //#endif
}