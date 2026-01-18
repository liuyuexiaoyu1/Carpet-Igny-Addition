package com.liuyue.igny.mixins.rule.safeSoundSuppression;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtils;
import net.minecraft.core.BlockPos;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
//#endif
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

    @Inject(method = "getBackSignal",at = @At("RETURN"), cancellable = true)
    private void getBackSignal(Level level, BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<Integer> cir) {
        if (this.igny$blockEntity == null) return;
        BlockPos blockEntityPos = igny$blockEntity.getBlockPos();
        //#if MC >= 12005
        Component component = this.igny$blockEntity.components().get(DataComponents.CUSTOM_NAME);
        //#endif
        if (!level.getBlockState(blockEntityPos).is(Blocks.CALIBRATED_SCULK_SENSOR)
                //#if MC >= 12005
                || (component != null && RuleUtils.canSoundSuppression(component.getString()))
                //#endif
        ) {
            if (cir.getReturnValue() == 0 && IGNYSettings.safeSoundSuppression) {
                cir.setReturnValue(16);
            }
        }
    }
}
