package com.liuyue.igny.utils;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
//#if MC >= 26.1
//$$ import net.minecraft.core.component.DataComponents;
//#else
import net.minecraft.world.item.DyeItem;
//#endif
import org.jetbrains.annotations.Nullable;

public final class ItemUtil {

    @Nullable
    public static DyeColor dyeOf(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        //#if MC >= 26.1
        //$$ return stack.get(DataComponents.DYE);
        //#else
        return stack.getItem() instanceof DyeItem dyeItem ? dyeItem.getDyeColor() : null;
        //#endif
    }
}
