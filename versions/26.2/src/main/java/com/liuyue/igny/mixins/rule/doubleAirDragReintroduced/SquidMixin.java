package com.liuyue.igny.mixins.rule.doubleAirDragReintroduced;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.doubleAirDrag.AirDragHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Squid.class)
public abstract class SquidMixin extends AgeableWaterCreature {

    protected SquidMixin(EntityType<? extends AgeableWaterCreature> type, Level level) {
        super(type, level);
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/squid/Squid;setDeltaMovement(DDD)V"))
    private void igny$doubleAirDrag(Squid instance, double x, double y, double z, Operation<Void> original, @Local(name = "yd") double yd) {
        if (!IGNYSettings.DOUBLE_AIR_DRAG_REINTRODUCED.value()) {
            original.call(instance, x, y, z);
            return;
        }
        original.call(instance, 0.0, yd * AirDragHelper.restoreDouble(this.getAirDrag()), 0.0);
    }
}
