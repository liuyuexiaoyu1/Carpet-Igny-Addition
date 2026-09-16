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
//#if MC >= 26.3
//$$ import net.minecraft.util.Prediction;
//#endif

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

        if (written == null || grid == null) {
            ci.cancel();
            return;
        }

        int layers = 0;
        int applications = 0;

        for (int i = 0; i < written.length && i < grid.getContainerSize(); i++) {
            if (written[i] <= 0) {
                continue;
            }

            if (written[i] > applications) {
                applications = written[i];
            }

            int taken = written[i] - grid.getItem(i).getCount();

            if (taken > layers) {
                layers = taken;
            }
        }
        Slot result = menu.slots.getFirst();

        if (layers > 0) {
            int per = applications > 0 ? Math.max(1, result.getItem().getCount() / applications) : 1;
            int left = result.getItem().getCount() - layers * per;

            if (left <= 0) {
                result.set(ItemStack.EMPTY);
                this.igny$setUncraftCandidates(null);
                this.igny$setUncraftBase(null);
                this.igny$setUncraftWritten(null);
                ci.cancel();
                return;
            }

            ItemStack products = UncraftingTable.single(result.getItem());
            products.setCount(left);
            result.set(products);
            result.setChanged();
        }

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
            //#if MC >= 26.3
            //$$ player.drop(products, false, Prediction.PREDICTED);
            //#else
            player.drop(products, false);
            //#endif
        }

        menu.slots.getFirst().setChanged();
    }
}