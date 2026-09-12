package com.liuyue.igny.mixins.rule.itemFlowTracker;

import com.liuyue.igny.utils.ItemUtil;
import com.liuyue.igny.utils.itemFlowTracker.ItemFlowTrackerSettings;
import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Shadow
    public abstract ItemStack getCarried();

    @Unique
    @Nullable
    private ItemStack[] igny$before;

    @Unique
    @Nullable
    private ItemStack igny$beforeCarried;

    @Inject(method = "clicked", at = @At(value = "HEAD"))
    private void igny$snapshot(CallbackInfo ci, @Local(argsOnly = true) Player player) {
        if (!ItemFlowTrackerSettings.enabled() || player == null || player.level().isClientSide()) {
            return;
        }

        ItemStack[] snapshot = new ItemStack[this.slots.size()];

        for (int i = 0; i < snapshot.length; i++) {
            snapshot[i] = this.slots.get(i).getItem().copy();
        }

        this.igny$before = snapshot;
        this.igny$beforeCarried = this.getCarried().copy();
    }

    @Inject(method = "clicked", at = @At(value = "RETURN"))
    private void igny$afterClick(CallbackInfo ci, @Local(argsOnly = true) Player player) {
        ItemStack[] before = this.igny$before;
        ItemStack beforeCarried = this.igny$beforeCarried;
        this.igny$before = null;
        this.igny$beforeCarried = null;

        if (before == null || player == null || !ItemFlowTrackerSettings.enabled() || player.level().isClientSide()) {
            return;
        }

        DyeColor dye = ItemUtil.dyeOf(player.getOffhandItem());
        Container playerInventory = player.getInventory();

        for (int i = 0; i < this.slots.size() && i < before.length; i++) {
            Slot slot = this.slots.get(i);

            if (slot.container == playerInventory) {
                continue;
            }

            ItemStack now = slot.getItem();

            if (now.isEmpty()) {
                continue;
            }

            ItemStack old = before[i];
            boolean sameStack = igny$sameStack(old, now);
            int arrived = sameStack ? now.getCount() - old.getCount() : now.getCount();

            if (arrived <= 0) {
                continue;
            }

            TrackMark mark = Tracking.get(now);

            if (mark != null) {
                if (old.isEmpty() || Tracking.has(old, mark)) {
                    Tracking.arrive(now, mark, arrived);
                }

                TrackingWatch.onEnterContainer(slot.container, null, null, mark);
                continue;
            }

            if (dye != null) {
                TrackMark fresh = Tracking.newMark(dye, arrived);
                Tracking.arrive(now, fresh, arrived);
                TrackingWatch.onEnterContainer(slot.container, null, null, fresh);
                continue;
            }

            ItemStack incoming = igny$incomingStack(before, beforeCarried, now);

            if (incoming != null) {
                Tracking.arrive(now, incoming, arrived);
                TrackingWatch.onEnterContainer(slot.container, null, null, Tracking.get(now));
            } else if (Nesting.carriesMark(now)) {
                TrackingWatch.onEnterContainer(slot.container, null, null, Nesting.inStack(now));
            }
        }
    }

    @Unique
    @Nullable
    private ItemStack igny$incomingStack(ItemStack[] before, @Nullable ItemStack beforeCarried, ItemStack destination) {
        if (Tracking.isMarked(beforeCarried) && igny$sameStack(beforeCarried, destination)) {
            return beforeCarried;
        }

        for (ItemStack candidate : before) {
            if (Tracking.isMarked(candidate) && igny$sameStack(candidate, destination)) {
                return candidate;
            }
        }

        return null;
    }

    @Unique
    private static boolean igny$sameStack(@Nullable ItemStack first, ItemStack second) {
        if (first == null || first.isEmpty() || second.isEmpty()) {
            return false;
        }

        //#if MC >= 12005
        return ItemStack.isSameItemSameComponents(first, second);
        //#else
        //$$ return ItemStack.isSameItemSameTags(first, second);
        //#endif
    }
}
