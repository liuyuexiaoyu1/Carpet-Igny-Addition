package com.liuyue.igny.helper.inventory;

//#if MC >= 12005
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DecoratedPotMenu extends AbstractContainerMenu {
    private final Container pot;

    public DecoratedPotMenu(int id, Inventory playerInv, Container pot) {
        super(MenuType.GENERIC_9x3, id);
        this.pot = pot;

        for (int i = 0; i < 27; i++) {
            if (i == DecoratedPotContainer.REAL_SLOT) {
                this.addSlot(new Slot(pot, DecoratedPotContainer.REAL_SLOT, 8 + (i % 9) * 18, 18 + (i / 9) * 18));
            } else {
                this.addSlot(new Slot(pot, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(@NotNull Player player) {
                        return false;
                    }
                });
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index == DecoratedPotContainer.REAL_SLOT) {
            if (!this.moveItemStackTo(stack, 27, 63, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= 27) {
            if (!this.moveItemStackTo(stack, DecoratedPotContainer.REAL_SLOT, DecoratedPotContainer.REAL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.pot.stillValid(player);
    }
}
//#endif