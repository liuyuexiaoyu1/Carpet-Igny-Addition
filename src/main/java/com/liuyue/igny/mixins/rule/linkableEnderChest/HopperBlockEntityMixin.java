package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = HopperBlockEntity.class, priority = 999)
public class HopperBlockEntityMixin {
    @WrapMethod(method = "suckInItems")
    private static boolean getSourceContainer(Level level, Hopper hopper, Operation<Boolean> original) {
        try {
            BlockPos blockPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.is(Blocks.ENDER_CHEST)) {
                if (!LinkedContainerManager.isRuleFully()) {
                    IGNYSettings.canGetBlockEntity.set(false);
                    return original.call(level, hopper);
                }
                if (level.getBlockEntity(blockPos) instanceof LinkedEnderChest enderChest) {
                    if (enderChest.carpet_Igny_Addition$isLinked()) {
                        List<ItemEntity> items = HopperBlockEntity.getItemsAtAndAbove(level, hopper);
                        if (!items.isEmpty()) {
                            IGNYSettings.canGetBlockEntity.set(false);
                            return original.call(level, hopper);
                        }
                    } else {
                        IGNYSettings.canGetBlockEntity.set(false);
                        return original.call(level, hopper);
                    }
                }
            }
            return original.call(level, hopper);
        } finally {
            IGNYSettings.canGetBlockEntity.set(true);
        }
    }
}
