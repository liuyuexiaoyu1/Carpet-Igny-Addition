package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.ItemFlowTrackerSettings;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12105
//$$ @Mixin(LivingEntity.class)
//#else
@Mixin(Player.class)
//#endif
public abstract class DropFastMarkMixin {
    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "HEAD")
    )
    private void igny$markThrownItem(ItemStack stack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!ItemFlowTrackerSettings.fastMark() || !ItemFlowTrackerSettings.enabled()) {
            return;
        }

        if (self.level().isClientSide() || stack.isEmpty() || !self.isAlive()) {
            return;
        }

        ItemStack offhand = self.getOffhandItem();

        if (stack == offhand) {
            return;
        }

        DyeColor dye = Tracking.dyeOf(offhand);

        if (dye != null && Tracking.get(stack) == null) {
            Tracking.set(stack, Tracking.newMark(dye, stack.getCount()));
        }
    }
}
