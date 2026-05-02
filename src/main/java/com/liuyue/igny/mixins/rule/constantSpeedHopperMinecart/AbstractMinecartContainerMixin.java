package com.liuyue.igny.mixins.rule.constantSpeedHopperMinecart;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMinecartContainer.class)
public class AbstractMinecartContainerMixin {
    @WrapOperation(method = "applyNaturalSlowdown", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;getRedstoneSignalFromContainer(Lnet/minecraft/world/Container;)I"))
    private int getRedstoneSignalFromContainer(Container container, Operation<Integer> original) {
        return IGNYSettings.constantSpeedHopperMinecart ? 15 : original.call(container);
    }
}
