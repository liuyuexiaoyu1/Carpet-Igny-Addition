package com.liuyue.igny.utils;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem; //#replace >= 26.1 ? import net.minecraft.core.component.DataComponents;
import org.jetbrains.annotations.Nullable;

public final class ItemUtil {

    @Nullable
    public static DyeColor dyeOf(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        return stack.getItem() instanceof DyeItem dyeItem ? dyeItem.getDyeColor() : null; //#replace >= 26.1 ? return stack.get(DataComponents.DYE);
    }
}
