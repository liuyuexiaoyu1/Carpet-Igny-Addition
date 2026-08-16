package com.liuyue.igny.mixins.rule.disableTntDispense;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.WrapDispenseItemBehavior;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenseItemBehavior.class)
public interface DispenseItemBehaviorMixin {
    @WrapOperation(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DispenserBlock;registerBehavior(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/core/dispenser/DispenseItemBehavior;)V"))
    private static void registerBehavior(ItemLike item, DispenseItemBehavior behavior, Operation<Void> original) {
        if (item.asItem().equals(Items.TNT) && behavior instanceof DefaultDispenseItemBehavior defaultDispenseItemBehavior) {
            original.call(item, WrapDispenseItemBehavior.withBeforeExecutor(defaultDispenseItemBehavior, (source, stack) -> {
                if (IGNYSettings.DISABLE_TNT_DISPENSE.value()) {
                    return new DefaultDispenseItemBehavior().dispense(source, stack);
                }
                return null;
            }));
            return;
        }
        original.call(item, behavior);
    }
}
