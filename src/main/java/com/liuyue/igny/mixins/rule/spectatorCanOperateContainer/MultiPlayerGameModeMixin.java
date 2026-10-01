package com.liuyue.igny.mixins.rule.spectatorCanOperateContainer;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.SpectatorContainerTarget;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @WrapOperation(method = "performUseItemOn", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;localPlayerMode:Lnet/minecraft/world/level/GameType;", opcode = Opcodes.GETFIELD, ordinal = 0))
    private GameType igny$spectatorOpenContainer(MultiPlayerGameMode instance, Operation<GameType> original) {
        GameType gameMode = original.call(instance);
        if (IGNYSettings.SPECTATOR_CAN_OPERATE_CONTAINER.value() && igny$isContainerTarget() && gameMode == GameType.SPECTATOR) {
            return GameType.SURVIVAL;
        }
        return gameMode;
    }

    @Unique
    private boolean igny$isContainerTarget() {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.hitResult instanceof BlockHitResult blockHit)) {
            return false;
        }
        return SpectatorContainerTarget.isContainerTarget(minecraft.level, blockHit.getBlockPos());
    }
}
