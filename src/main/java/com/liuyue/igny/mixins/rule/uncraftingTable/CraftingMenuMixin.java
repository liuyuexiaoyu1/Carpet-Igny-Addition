package com.liuyue.igny.mixins.rule.uncraftingTable;

import com.liuyue.igny.utils.uncraftingTable.UncraftingState;
import com.liuyue.igny.utils.uncraftingTable.UncraftingTable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?>= 26.3 ? import net.minecraft.util.Prediction;

import java.util.List;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin implements UncraftingState {

    @Unique
    @Nullable
    private List<?> igny$uncraftCandidates;

    @Unique
    private int igny$uncraftIndex;

    @Unique
    @Nullable
    private ItemStack[] igny$uncraftBase;

    @Unique
    private int @Nullable [] igny$uncraftWritten;

    @Unique
    private int igny$uncraftPer = 1;

    @Override
    public int igny$uncraftPer() {
        return this.igny$uncraftPer;
    }

    @Override
    public void igny$setUncraftPer(int per) {
        this.igny$uncraftPer = Math.max(1, per);
    }

    @Override
    @Nullable
    public List<?> igny$uncraftCandidates() {
        return this.igny$uncraftCandidates;
    }

    @Override
    public void igny$setUncraftCandidates(@Nullable List<?> candidates) {
        this.igny$uncraftCandidates = candidates;
    }

    @Override
    public int igny$uncraftIndex() {
        return this.igny$uncraftIndex;
    }

    @Override
    public void igny$setUncraftIndex(int index) {
        this.igny$uncraftIndex = index;
    }

    @Override
    @Nullable
    public ItemStack[] igny$uncraftBase() {
        return this.igny$uncraftBase;
    }

    @Override
    public void igny$setUncraftBase(@Nullable ItemStack[] base) {
        this.igny$uncraftBase = base;
    }

    @Override
    public int @Nullable [] igny$uncraftWritten() {
        return this.igny$uncraftWritten;
    }

    @Override
    public void igny$setUncraftWritten(int @Nullable [] written) {
        this.igny$uncraftWritten = written;
    }

    @Inject(method = "slotsChanged", at = @At("HEAD"), cancellable = true)
    private void igny$onGridChanged(Container container, CallbackInfo ci) {
        if (!this.igny$uncrafting()) {
            return;
        }

        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;

        if (menu.slots.size() < 10) {
            return;
        }

        if (UncraftingTable.isFilling()) {
            ci.cancel();
            return;
        }

        int[] written = this.igny$uncraftWritten();
        CraftingContainer grid = UncraftingTable.gridContainer(menu);
        ItemStack[] base = this.igny$uncraftBase();

        if (grid == null || base == null) {
            ci.cancel();
            return;
        }

        Slot result = menu.slots.getFirst();

        if (result.getItem().isEmpty()) {
            ci.cancel();
            return;
        }

        int factor = Integer.MAX_VALUE;
        boolean any = false;

        for (int i = 0; i < base.length && i < grid.getContainerSize(); i++) {
            ItemStack material = base[i];

            if (material == null || material.isEmpty()) {
                continue;
            }

            any = true;
            factor = Math.min(factor, grid.getItem(i).getCount() / Math.max(1, material.getCount()));
        }

        if (!any) {
            ci.cancel();
            return;
        }

        if (factor == Integer.MAX_VALUE) {
            factor = 0;
        }

        int products = factor * this.igny$uncraftPer();

        if (products <= 0) {
            result.set(ItemStack.EMPTY);
            this.igny$setUncraftCandidates(null);
            this.igny$setUncraftBase(null);
            this.igny$setUncraftWritten(null);
            ci.cancel();
            return;
        }

        ItemStack shown = result.getItem().copy();
        shown.setCount(products);
        result.set(shown);
        result.setChanged();

        UncraftingTable.readGrid(menu, written);
        ci.cancel();
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void igny$returnQueryItem(Player player, CallbackInfo ci) {
        if (!this.igny$uncrafting()) {
            return;
        }

        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;

        if (menu.slots.isEmpty() || !(menu.slots.getFirst() instanceof ResultSlot result)) {
            return;
        }

        ItemStack products = result.getItem();
        result.set(ItemStack.EMPTY);

        UncraftingTable.clearGrid(menu);

        this.igny$setUncraftCandidates(null);
        this.igny$setUncraftBase(null);
        this.igny$setUncraftWritten(null);

        if (!products.isEmpty() && !player.getInventory().add(products)) {
            player.drop(products, false); //#replace >= 26.3 ? player.drop(products, false, Prediction.PREDICTED);
        }

        menu.slots.getFirst().setChanged();
    }
}