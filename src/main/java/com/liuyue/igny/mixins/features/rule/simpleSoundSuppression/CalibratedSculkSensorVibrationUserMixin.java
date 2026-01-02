package com.liuyue.igny.mixins.features.rule.simpleSoundSuppression;

import com.liuyue.igny.exception.IAEUpdateSuppressException;
import com.liuyue.igny.utils.RuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/level/block/entity/CalibratedSculkSensorBlockEntity$VibrationUser")
public class CalibratedSculkSensorVibrationUserMixin {
    @Unique
    private CalibratedSculkSensorBlockEntity igny$blockEntity;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void createBlockEntity(CalibratedSculkSensorBlockEntity calibratedSculkSensorBlockEntity, BlockPos blockPos, CallbackInfo ci) {
        this.igny$blockEntity = calibratedSculkSensorBlockEntity;
    }

    @Inject(method = "canReceiveVibration", at = @At("RETURN"))
    private void canReceiveVibration(ServerLevel serverLevel, BlockPos blockPos, Holder<GameEvent> holder, GameEvent.Context context, CallbackInfoReturnable<Boolean> cir) {
        if (this.igny$blockEntity == null) return;
        Component component = this.igny$blockEntity.components().get(DataComponents.CUSTOM_NAME);
        if (component != null) {
            if (RuleUtils.canSoundSuppression(component.getString()) && cir.getReturnValueZ()) {
                if (serverLevel.isClientSide()) return;
                throw new IAEUpdateSuppressException("Sound Suppression Update Suppress triggered on " + serverLevel.dimension().location() + "[" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ() + "]");
            }
        }
    }
}
