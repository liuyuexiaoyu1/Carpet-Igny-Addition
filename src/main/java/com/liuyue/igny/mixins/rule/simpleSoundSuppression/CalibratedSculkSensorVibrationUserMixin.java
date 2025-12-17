package com.liuyue.igny.mixins.rule.simpleSoundSuppression;

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
    private CalibratedSculkSensorBlockEntity igny$capturedSensor;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void igny$onInit(CalibratedSculkSensorBlockEntity calibratedSculkSensorBlockEntity, BlockPos blockPos, CallbackInfo ci) {
        this.igny$capturedSensor = calibratedSculkSensorBlockEntity;
    }

    @Inject(method = "canReceiveVibration", at = @At("RETURN"))
    private void canReceiveVibration(ServerLevel serverLevel, BlockPos blockPos, Holder<GameEvent> holder, GameEvent.Context context, CallbackInfoReturnable<Boolean> cir) {
        if (this.igny$capturedSensor == null) return;
        Component component = this.igny$capturedSensor.components().get(DataComponents.CUSTOM_NAME);
        if (component != null) {
            if (RuleUtils.canSoundSuppression(component.getString()) && cir.getReturnValueZ()) {
                throw new IllegalArgumentException("[Carpet-Igny-Addition] Simple SoundSuppression");
            }
        }
    }
}
