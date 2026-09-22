package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.ItemUtil;
import com.liuyue.igny.utils.itemFlowTracker.ItemFlowTrackerSettings;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?>= 26.3 ? import net.minecraft.util.Prediction;

@Mixin(Player.class) //#replace >= 1.21.5 ? @Mixin(LivingEntity.class)
public abstract class DropFastMarkMixin {
    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", //#replace >= 26.3 ? method = "drop",
            at = @At(value = "HEAD")
    )
    private void igny$markThrownItem(ItemStack stack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) //#replace >= 26.3 ? private void igny$markThrownItem(ItemStack stack, boolean thrownFromHand, Prediction prediction, CallbackInfoReturnable<ItemEntity> cir)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        TrackingWatch.beginHandDrop();

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

        DyeColor dye = ItemUtil.dyeOf(offhand);

        if (dye != null && Tracking.get(stack) == null) {
            Tracking.set(stack, Tracking.newMark(dye, stack.getCount()));
        }
    }

    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", //#replace >= 26.3 ? method = "drop",
            at = @At(value = "RETURN")
    )
    private void igny$endHandDrop(ItemStack stack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) //#replace >= 26.3 ? private void igny$endHandDrop(ItemStack stack, boolean thrownFromHand, Prediction prediction, CallbackInfoReturnable<ItemEntity> cir)
    {
        TrackingWatch.endHandDrop();
    }
}
