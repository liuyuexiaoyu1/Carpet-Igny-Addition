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
//#if MC >= 26.3
//$$ import net.minecraft.util.Prediction;
//#endif

//#if MC >= 12105
//$$ @Mixin(LivingEntity.class)
//#else
@Mixin(Player.class)
//#endif
public abstract class DropFastMarkMixin {
    @Inject(
            //#if MC >= 26.3
            //$$ method = "drop",
            //#else
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            //#endif
            at = @At(value = "HEAD")
    )
    //#if MC >= 26.3
    //$$ private void igny$markThrownItem(ItemStack stack, boolean thrownFromHand, Prediction prediction, CallbackInfoReturnable<ItemEntity> cir)
    //#else
    private void igny$markThrownItem(ItemStack stack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir)
    //#endif
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
            //#if MC >= 26.3
            //$$ method = "drop",
            //#else
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            //#endif
            at = @At(value = "RETURN")
    )
    //#if MC >= 26.3
    //$$ private void igny$endHandDrop(ItemStack stack, boolean thrownFromHand, Prediction prediction, CallbackInfoReturnable<ItemEntity> cir)
    //#else
    private void igny$endHandDrop(ItemStack stack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir)
    //#endif
    {
        TrackingWatch.endHandDrop();
    }
}
