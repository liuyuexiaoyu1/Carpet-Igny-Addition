package com.liuyue.igny.mixins.rule.waterCanPushSquid;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Squid.class)
public class SquidMixin extends WaterAnimal {
    protected SquidMixin(EntityType<? extends WaterAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "travel", at = @At(value = "HEAD"), cancellable = true)
    private void travel(Vec3 travelVector, CallbackInfo ci) {
        if (IGNYSettings.WATER_CAN_PUSH_SQUID.value()) {
            super.travel(travelVector);
            ci.cancel();
        }
    }
}
