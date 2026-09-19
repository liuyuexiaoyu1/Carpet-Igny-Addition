package com.liuyue.igny.mixins.rule.uncraftingTable;

import com.liuyue.igny.utils.uncraftingTable.UncraftingTable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC < 12005
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.world.level.Level;
//#else
import net.minecraft.world.item.crafting.RecipeHolder;
//#endif

import java.util.List;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {
    //#if MC >= 12005
    @Shadow
    protected abstract void dispenseItem(ServerLevel level, BlockPos pos, CrafterBlockEntity crafter, ItemStack stack, BlockState state, RecipeHolder<?> recipe);
    //#else
    //$$ @Shadow
    //$$ protected abstract void dispenseItem(Level level, BlockPos pos, CrafterBlockEntity crafter, ItemStack stack, BlockState state);
    //#endif

    @Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
    private void dispenseFrom(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (!(level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter)) {
            return;
        }

        Component name = crafter.getCustomName();

        if (UncraftingTable.isUncraftMode(name)) {
            ci.cancel();
            int index = UncraftingTable.indexOf(name) - 1;
            ItemStack input = crafter.getItem(UncraftingTable.CRAFTER_RESULT_SLOT);

            if (input.isEmpty()) {
                return;
            }

            List<?> candidates = UncraftingTable.candidates(level, input);

            if (index < 0 || index >= candidates.size()) {
                return;
            }

            Object holder = candidates.get(index);
            CraftingRecipe recipe = UncraftingTable.recipeOf(holder);
            ItemStack[] base = recipe == null ? null : UncraftingTable.decompose(recipe);

            if (base == null) {
                return;
            }

            int per = Math.max(1, UncraftingTable.outputCount(level, holder));

            if (input.getCount() < per) {
                return;
            }

            int applications = 1;

            input.shrink(per);
            crafter.setItem(UncraftingTable.CRAFTER_RESULT_SLOT, input.isEmpty() ? ItemStack.EMPTY : input);

            for (ItemStack material : base) {
                if (material.isEmpty()) {
                    continue;
                }

                int remaining = material.getCount() * applications;
                int max = Math.max(1, material.getMaxStackSize());

                while (remaining > 0) {
                    int chunk = Math.min(remaining, max);
                    //#if MC >= 12005
                    this.dispenseItem(level, pos, crafter, material.copyWithCount(chunk), state, (RecipeHolder<?>) holder);
                    //#else
                    //$$ this.dispenseItem(level, pos, crafter, material.copyWithCount(chunk), state);
                    //#endif
                    remaining -= chunk;
                }
            }
        }
    }
}
