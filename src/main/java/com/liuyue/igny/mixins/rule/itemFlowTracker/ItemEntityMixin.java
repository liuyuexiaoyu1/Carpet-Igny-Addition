package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Unique
    @Nullable
    private TrackMark igny$applied;

    @Unique
    private boolean igny$hasApplied;

    @Inject(
            method = {
                    "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V",
                    "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V"
            },
            at = @At(value = "TAIL")
    )
    private void igny$refundOnDrop(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;

        if (!self.level().isClientSide() && Tracking.isMarked(self.getItem())) {
            Tracking.moved(self.getItem());
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void igny$syncWatch(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;

        if (self.level().isClientSide()) {
            return;
        }

        TrackMark mark = Nesting.inStack(self.getItem());

        if (this.igny$hasApplied && Objects.equals(this.igny$applied, mark)) {
            return;
        }

        if (mark != null) {
            TrackingWatch.watchEntity(self);
        }

        this.igny$applied = mark;
        this.igny$hasApplied = true;
    }

    @Inject(
            method = "merge(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "RETURN")
    )
    private static void igny$merge(ItemStack destination, ItemStack source, int limit, CallbackInfoReturnable<ItemStack> cir) {
        TrackMark mark = Tracking.getRaw(source);
        ItemStack merged = cir.getReturnValue();

        if (mark != null && merged != null) {
            Tracking.arrive(merged, mark, merged.getCount() - destination.getCount());
        }
    }
}
