package com.liuyue.igny.mixins.rule.alwaysAvailableGameMasterBlocks;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.rule.CommandPermissionLevel;
import com.liuyue.igny.utils.CommandUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//#if >= 1.21.11
/*$$import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionSet;$$*/
//#endif

@Mixin(Player.class)
public class PlayerMixin {
    @WrapOperation(method = "canUseGameMasterBlocks", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z", opcode = Opcodes.GETFIELD))
    private boolean canUseGameMasterBlocks(Abilities instance, Operation<Boolean> original) {
        if (!IGNYSettings.ALWAYS_AVAILABLE_GAME_MASTER_BLOCKS.value().equals(CommandPermissionLevel.FALSE)) {
            return true;
        }
        return original.call(instance);
    }

    //#if >= 1.21.11
    /*$$@WrapOperation(method = "canUseGameMasterBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/permissions/PermissionSet;hasPermission(Lnet/minecraft/server/permissions/Permission;)Z"))
    private boolean getPermissionLevel(PermissionSet instance, Permission permission, Operation<Boolean> original)$$*/
    //#else
    @WrapOperation(method = "canUseGameMasterBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getPermissionLevel()I"))
    private int getPermissionLevel(Player instance, Operation<Integer> original)
    //#endif
    {
        if (CommandUtil.canUseCommand((Player) (Object) this, IGNYSettings.ALWAYS_AVAILABLE_GAME_MASTER_BLOCKS.value())){
            return 4; //#replace >= 1.21.11 ? return true;
        }
        return original.call(instance); //#replace >= 1.21.11 ? return original.call(instance, permission);
    }
}
