package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackedStack;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements TrackedStack {
    @Unique
    @Nullable
    private List<TrackMark> igny$marks;

    @Override
    public @Nullable List<TrackMark> igny$getMarks() {
        return this.igny$marks;
    }

    @Override
    public void igny$setMarks(@Nullable List<TrackMark> marks) {
        this.igny$marks = marks;
    }

    @Inject(method = "copy", at = @At(value = "RETURN"))
    private void igny$copy(CallbackInfoReturnable<ItemStack> cir) {
        Tracking.spread((ItemStack) (Object) this, cir.getReturnValue());
    }

    //#if >= 1.20.1
    @Inject(method = "copyAndClear", at = @At(value = "RETURN"))
    private void igny$copyAndClear(CallbackInfoReturnable<ItemStack> cir) {
        Tracking.spread((ItemStack) (Object) this, cir.getReturnValue());
    }
    //#endif

    @Inject(method = "copyWithCount", at = @At(value = "RETURN"))
    private void igny$copyWithCount(int count, CallbackInfoReturnable<ItemStack> cir) {
        Tracking.spread((ItemStack) (Object) this, cir.getReturnValue());
    }

    @Inject(method = "split", at = @At(value = "RETURN"))
    private void igny$split(int count, CallbackInfoReturnable<ItemStack> cir) {
        Tracking.onSplit((ItemStack) (Object) this, cir.getReturnValue());
    }

    //#if >= 1.20.6
    @Inject(method = "transmuteCopyIgnoreEmpty", at = @At(value = "RETURN"))
    private void igny$transmuteCopy(ItemLike item, int count, CallbackInfoReturnable<ItemStack> cir) {
        Tracking.spread((ItemStack) (Object) this, cir.getReturnValue());
    }
    //#endif
}
