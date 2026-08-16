package com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemStackProtocolDataAdapter {
    int igny$toProtocolValueAddition(ItemStack fromStack);

    @NotNull
    ItemStack igny$fromProtocolValueAddition(int extraProtocolValue, ItemStack fromStack);
}