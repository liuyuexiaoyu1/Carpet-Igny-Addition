package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = HopperBlockEntity.class, priority = 1001)
public class HopperBlockEntityMixin {
    @WrapMethod(method = "getSourceContainer")
    private static Container getSourceContainer(Level level, Hopper hopper, BlockPos pos, BlockState state, Operation<Container> original) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.is(Blocks.ENDER_CHEST)) {
            if (!LinkedContainerManager.isRuleFully()) {
                return null;
            }
            if (level.getBlockEntity(pos) instanceof LinkedEnderChest enderChest) {
                if (enderChest.carpet_Igny_Addition$isLinked()) {
                    List<ItemEntity> items = HopperBlockEntity.getItemsAtAndAbove(level, hopper);
                    if (!items.isEmpty()) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
        return original.call(level, hopper, pos, state);
    }
}
