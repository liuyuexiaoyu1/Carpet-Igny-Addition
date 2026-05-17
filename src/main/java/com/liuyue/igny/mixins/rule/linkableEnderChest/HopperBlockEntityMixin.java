package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(value = HopperBlockEntity.class, priority = 1001)
public class HopperBlockEntityMixin {
    @ModifyVariable(method = "suckInItems", at = @At(value = "STORE"))
    private static Container getSourceContainer(Container value, @Local(argsOnly = true) Level level, @Local(argsOnly = true) Hopper hopper) {
        BlockPos blockPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.is(Blocks.ENDER_CHEST)) {
            if (!LinkedContainerManager.isRuleFully()) {
                return null;
            }
            if (level.getBlockEntity(blockPos) instanceof LinkedEnderChest enderChest) {
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
        return value;
    }
}
