package com.liuyue.igny.mixins.rule.uncraftingTable;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.uncraftingTable.UncraftingState;
import com.liuyue.igny.utils.uncraftingTable.UncraftingTable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
//#if MC >= 26.1
//$$ import net.minecraft.world.inventory.ContainerInput;
//#else
import net.minecraft.world.inventory.ClickType;
//#endif
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractContainerMenu.class)
public abstract class UncraftingMenuMixin {

    //#if MC >= 26.1
    //$$ @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    //$$ private void igny$uncraftingClick(int slotId, int button, ContainerInput clickType, Player player, CallbackInfo ci) {
    //#else
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void igny$uncraftingClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
    //#endif
        if (player.level().isClientSide()) {
            return;
        }

        if (!IGNYSettings.UNCRAFTING_TABLE.value()) {
            return;
        }

        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;

        if (!(menu instanceof CraftingMenu) || !UncraftingTable.isUncraftable(menu)) {
            return;
        }

        if (!(menu instanceof UncraftingState state)) {
            return;
        }

        if (menu.slots.size() < 10) {
            return;
        }

        Slot result = menu.slots.getFirst();

        if (!(result.container instanceof ResultContainer)) {
            return;
        }

        if (slotId != 0) {
            return;
        }

        ItemStack products = result.getItem();
        ItemStack carried = menu.getCarried();
        boolean sameItem = !products.isEmpty() && !carried.isEmpty() && products.getItem() == carried.getItem();

        if (igny$isPickup(clickType) && !carried.isEmpty() && (products.isEmpty() || sameItem)) {
            int amount = button == 0 ? carried.getCount() : 1;

            if (products.isEmpty() && !state.igny$uncrafting()) {
                ItemStack placed = UncraftingTable.single(carried);
                placed.setCount(amount);
                List<?> candidates = UncraftingTable.candidates(player.level(), placed);
                carried.shrink(amount);
                menu.setCarried(carried);
                result.set(placed);
                state.igny$setUncraftCandidates(candidates);
                state.igny$setUncraftIndex(0);
                int[] written = new int[9];
                state.igny$setUncraftWritten(written);
                igny$rewrite(menu, state, player, candidates.isEmpty() ? null : candidates.getFirst(), amount);
                result.setChanged();
                menu.broadcastChanges();
                ci.cancel();
                return;
            }

            if (sameItem && state.igny$uncrafting()) {
                int room = Math.min(amount, products.getMaxStackSize() - products.getCount());

                if (room > 0) {
                    products.grow(room);
                    carried.shrink(room);
                    menu.setCarried(carried);
                    List<?> current = state.igny$uncraftCandidates();
                    Object selected = current == null || current.isEmpty() ? null : current.get(Math.min(state.igny$uncraftIndex(), current.size() - 1));
                    List<?> refreshed = UncraftingTable.candidates(player.level(), products);
                    int at = selected == null ? -1 : refreshed.indexOf(selected);
                    state.igny$setUncraftCandidates(refreshed);
                    state.igny$setUncraftIndex(Math.max(at, 0));
                    igny$rewrite(menu, state, player, refreshed.isEmpty() ? null : refreshed.get(Math.max(at, 0)), products.getCount());
                    result.setChanged();
                    menu.broadcastChanges();
                }

                ci.cancel();
                return;
            }
        }

        if (products.isEmpty() && !carried.isEmpty()) {
            ci.cancel();
            return;
        }

        if (!state.igny$uncrafting() || products.isEmpty()) {
            return;
        }

        if (igny$isThrow(clickType) && carried.isEmpty()) {
            List<?> candidates = state.igny$uncraftCandidates();

            if (candidates == null || candidates.isEmpty()) {
                return;
            }

            Object selected = candidates.get(Math.min(state.igny$uncraftIndex(), candidates.size() - 1));
            List<?> refreshed = UncraftingTable.candidates(player.level(), products);
            state.igny$setUncraftCandidates(refreshed);

            if (refreshed.isEmpty()) {
                UncraftingTable.clearGrid(menu);
                state.igny$setUncraftBase(null);
                UncraftingTable.readGrid(menu, state.igny$uncraftWritten());
                result.setChanged();
                menu.broadcastChanges();
                ci.cancel();
                return;
            }

            int at = refreshed.indexOf(selected);
            int next = (Math.max(at, 0)) + 1;
            next %= refreshed.size();
            state.igny$setUncraftIndex(next);
            igny$rewrite(menu, state, player, refreshed.get(next), products.getCount());
            result.setChanged();
            menu.broadcastChanges();
            ci.cancel();
            return;
        }

        if (igny$isPickup(clickType) && carried.isEmpty() && button == 0) {
            result.set(ItemStack.EMPTY);
            UncraftingTable.clearGrid(menu);
            state.igny$setUncraftCandidates(null);
            state.igny$setUncraftBase(null);
            state.igny$setUncraftWritten(null);
            menu.setCarried(products);
            menu.broadcastChanges();
            ci.cancel();
            return;
        }

        if (igny$isQuickMove(clickType)) {
            result.set(ItemStack.EMPTY);
            UncraftingTable.clearGrid(menu);
            state.igny$setUncraftCandidates(null);
            state.igny$setUncraftBase(null);
            state.igny$setUncraftWritten(null);

            if (!player.getInventory().add(products)) {
                player.drop(products, false);
            }

            menu.broadcastChanges();
            ci.cancel();
        }
    }

    @Unique
    private void igny$rewrite(AbstractContainerMenu menu, UncraftingState state, Player player, @Nullable Object entry, int products) {
        int per = UncraftingTable.outputCount(player.level(), entry);
        state.igny$setUncraftBase(entry == null ? null : UncraftingTable.gridOf(entry));
        UncraftingTable.writeGrid(menu, state.igny$uncraftBase(), products / per);
        UncraftingTable.readGrid(menu, state.igny$uncraftWritten());
    }

    //#if MC >= 26.1
    //$$ @Unique
    //$$ private static boolean igny$isThrow(ContainerInput clickType) {
    //$$     return clickType == ContainerInput.THROW;
    //$$ }
    //$$
    //$$ @Unique
    //$$ private static boolean igny$isPickup(ContainerInput clickType) {
    //$$     return clickType == ContainerInput.PICKUP;
    //$$ }
    //$$
    //$$ @Unique
    //$$ private static boolean igny$isQuickMove(ContainerInput clickType) {
    //$$     return clickType == ContainerInput.QUICK_MOVE;
    //$$ }
    //#else
    @Unique
    private static boolean igny$isThrow(ClickType clickType) {
        return clickType == ClickType.THROW;
    }

    @Unique
    private static boolean igny$isPickup(ClickType clickType) {
        return clickType == ClickType.PICKUP;
    }

    @Unique
    private static boolean igny$isQuickMove(ClickType clickType) {
        return clickType == ClickType.QUICK_MOVE;
    }
    //#endif
}
