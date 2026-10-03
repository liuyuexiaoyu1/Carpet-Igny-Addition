package com.liuyue.igny.mixins.rule.spectatorCanOperateContainer;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.SpectatorContainerTarget;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @WrapOperation(method = "useItemOn", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;gameModeForPlayer:Lnet/minecraft/world/level/GameType;", opcode = Opcodes.GETFIELD, ordinal = 0))
    private GameType igny$spectatorOpenContainer(ServerPlayerGameMode instance, Operation<GameType> original,
                                                 @Local(argsOnly = true) Level level,
                                                 @Local(argsOnly = true) BlockHitResult hitResult) {
        GameType gameMode = original.call(instance);
        if (IGNYSettings.SPECTATOR_CAN_OPERATE_CONTAINER.value() && gameMode == GameType.SPECTATOR) {
            BlockPos pos = hitResult.getBlockPos();
            if (SpectatorContainerTarget.isContainerTarget(level, pos)) {
                return GameType.SURVIVAL;
            }
        }
        return gameMode;
    }
}
