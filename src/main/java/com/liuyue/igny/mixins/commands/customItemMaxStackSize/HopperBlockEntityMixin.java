// MIT License
//
// Copyright (c) 2024 fcsailboat
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.


package com.liuyue.igny.mixins.commands.customItemMaxStackSize;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HopperBlockEntity.class, priority = 940)
public abstract class HopperBlockEntityMixin extends BlockEntity {
    public HopperBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @WrapMethod(method = "pushItemsTick")
    private static void pushItemsTick(Level level, BlockPos blockPos, BlockState blockState, HopperBlockEntity hopperBlockEntity, Operation<Void> original) {
        boolean changed = IGNYSettings.itemStackCountChanged.get();
        try {
            IGNYSettings.itemStackCountChanged.set(false);
            original.call(level, blockPos, blockState, hopperBlockEntity);
        } finally {
            IGNYSettings.itemStackCountChanged.set(changed);
        }
    }

    @WrapMethod(method = "entityInside")
    private static void entityInside(Level level, BlockPos blockPos, BlockState blockState, Entity entity, HopperBlockEntity hopperBlockEntity, Operation<Void> original) {
        boolean changed = IGNYSettings.itemStackCountChanged.get();
        try {
            IGNYSettings.itemStackCountChanged.set(false);
            original.call(level, blockPos, blockState, entity, hopperBlockEntity);
        } finally {
            IGNYSettings.itemStackCountChanged.set(changed);
        }
    }

    @WrapOperation(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack extract(Container from, Container to, ItemStack stack, Direction side, Operation<ItemStack> original, @Local LocalBooleanRef bl) {
        int customMax = CustomItemMaxStackSizeDataManager.getCustomStackSize(stack);
        if (customMax != -1) {
            ItemStack split = stack.split(stack.getMaxStackSize());
            int count = split.getCount();
            ItemStack result = original.call(from, to, split.copy(), side);
            stack.grow(result.getCount());
            if (count != result.getCount()) {
                bl.set(true);
            }
            return stack;
        }
        return original.call(from, to, stack, side);
    }
}