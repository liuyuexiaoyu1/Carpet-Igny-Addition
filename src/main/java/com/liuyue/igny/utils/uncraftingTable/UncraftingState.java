package com.liuyue.igny.utils.uncraftingTable;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.world.item.ItemStack;

public interface UncraftingState {

    @Nullable
    List<?> igny$uncraftCandidates();

    void igny$setUncraftCandidates(@Nullable List<?> candidates);

    int igny$uncraftIndex();

    void igny$setUncraftIndex(int index);

    @Nullable
    ItemStack[] igny$uncraftBase();

    void igny$setUncraftBase(@Nullable ItemStack[] base);

    int @Nullable [] igny$uncraftWritten();

    void igny$setUncraftWritten(int @Nullable [] written);

    default boolean igny$uncrafting() {
        return this.igny$uncraftCandidates() != null;
    }
}
