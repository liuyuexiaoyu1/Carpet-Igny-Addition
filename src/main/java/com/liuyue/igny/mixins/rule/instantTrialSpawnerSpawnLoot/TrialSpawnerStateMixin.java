// MixinTrialSpawnerState.java
package com.liuyue.igny.mixins.rule.instantTrialSpawnerSpawnLoot;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrialSpawnerState.class)
public class TrialSpawnerStateMixin {

    @WrapOperation(
            method = "tickAndGetNext",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/block/entity/trialspawner/TrialSpawnerState;WAITING_FOR_REWARD_EJECTION:Lnet/minecraft/world/level/block/entity/trialspawner/TrialSpawnerState;",
                    ordinal = 0,
                    opcode = Opcodes.GETSTATIC
            )
    )
    private TrialSpawnerState noWait(Operation<TrialSpawnerState> original, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) ServerLevel serverLevel) {
        serverLevel.playSound(null, blockPos, SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER, SoundSource.BLOCKS);
        return TrialSpawnerState.EJECTING_REWARD;
    }

    @WrapOperation(method = "tickAndGetNext", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/trialspawner/TrialSpawnerData;isReadyToEjectItems(Lnet/minecraft/server/level/ServerLevel;FI)Z"))
    private boolean dropItem(TrialSpawnerData instance, ServerLevel serverLevel, float f, int i, Operation<Boolean> original) {
        if (IGNYSettings.instantTrialSpawnerSpawnLoot) {
            return true;
        }
        return original.call(instance, serverLevel, f, i);
    }
}