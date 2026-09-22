package com.liuyue.igny.helper.uncraftingTable;

import com.liuyue.igny.utils.uncraftingTable.UncraftingTable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class CrafterUncrafting {

    public record Result(List<ItemStack> outputs, Object holder) {
    }

    private CrafterUncrafting() {
    }

    @Nullable
    public static Result tryUncraft(ServerLevel level, CrafterBlockEntity crafter) {
        Component name = crafter.getCustomName();

        if (!UncraftingTable.isUncraftMode(name)) {
            return null;
        }

        int index = UncraftingTable.indexOf(name);
        ItemStack input = crafter.getItem(UncraftingTable.CRAFTER_RESULT_SLOT);

        if (input.isEmpty()) {
            return null;
        }

        List<?> candidates = UncraftingTable.candidates(level, input);

        if (index >= candidates.size()) {
            return null;
        }

        Object holder = candidates.get(index);
        CraftingRecipe recipe = UncraftingTable.recipeOf(holder);
        ItemStack[] base = recipe == null ? null : UncraftingTable.decompose(recipe);

        if (base == null) {
            return null;
        }

        int per = Math.max(1, UncraftingTable.outputCount(level, holder));
        int applications = input.getCount() / per;

        if (applications <= 0) {
            return null;
        }

        input.shrink(applications * per);
        crafter.setItem(UncraftingTable.CRAFTER_RESULT_SLOT, input.isEmpty() ? ItemStack.EMPTY : input);

        List<ItemStack> outputs = new ArrayList<>();

        for (ItemStack material : base) {
            if (material.isEmpty()) {
                continue;
            }

            int remaining = material.getCount() * applications;
            int max = Math.max(1, material.getMaxStackSize());

            while (remaining > 0) {
                int chunk = Math.min(remaining, max);
                outputs.add(material.copyWithCount(chunk));
                remaining -= chunk;
            }
        }

        return new Result(outputs, holder);
    }
}
