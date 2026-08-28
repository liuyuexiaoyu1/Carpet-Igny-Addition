package com.liuyue.igny.helper.inventory;

//#if MC >= 12005
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import org.jetbrains.annotations.NotNull;

public class DecoratedPotContainer implements Container {
    public static final int REAL_SLOT = 13;
    private static final ItemStack PLACEHOLDER = new ItemStack(Items.BARRIER, 1);

    private final DecoratedPotBlockEntity pot;

    public DecoratedPotContainer(DecoratedPotBlockEntity pot) {
        this.pot = pot;
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        return pot.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        if (index == REAL_SLOT) {
            return pot.getItem(0);
        }
        return PLACEHOLDER.copy();
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        if (index == REAL_SLOT) {
            return pot.removeItem(0, count);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        if (index == REAL_SLOT) {
            return pot.removeItemNoUpdate(0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == REAL_SLOT) {
            pot.setItem(0, stack);
        }
    }

    @Override
    public void setChanged() {
        pot.setChanged();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return index == REAL_SLOT;
    }

    @Override
    public boolean stillValid(Player player) {
        return pot.stillValid(player);
    }

    @Override
    public void clearContent() {
        pot.clearContent();
    }
}
//#endif
