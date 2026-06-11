package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = HopperBlockEntity.class, priority = 999)
public class HopperBlockEntityMixin {
    @Inject(method = "getBlockContainer", at = @At(value = "RETURN"), cancellable = true)
    private static void getBlockContainer(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Container> cir) {
        if (state.is(Blocks.ENDER_CHEST)) {
            if (!LinkedContainerManager.isRuleFully()) {
                cir.setReturnValue(null);
                return;
            }
            if (level.getBlockEntity(pos) instanceof LinkedEnderChest enderChest) {
                if (enderChest.carpet_Igny_Addition$isLinked()) {
                    List<ItemEntity> items = HopperBlockEntity.getItemsAtAndAbove(level, (HopperBlockEntity) enderChest);
                    if (!items.isEmpty()) {
                        cir.setReturnValue(null);
                    }
                }
            }
        }
    }
}
