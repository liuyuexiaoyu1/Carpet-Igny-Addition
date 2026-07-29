package com.liuyue.igny.mixins.rule.copperGolemDropOnlyNormal;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public class BlockMixin {
    //#if MC >= 26.1
    //$$ @ModifyVariable(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;", at = @At(value = "HEAD"), argsOnly = true)
    //#else
    @ModifyVariable(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "HEAD"), argsOnly = true)
    //#endif
    private static BlockState getDrops(BlockState state) {
        if (state.is(Blocks.COPPER_GOLEM_STATUE) && IGNYSettings.COPPER_GOLEM_DROP_ONLY_NORMAL.value()) {
            return state.setValue(CopperGolemStatueBlock.POSE, CopperGolemStatueBlock.Pose.STANDING);
        }
        return state;
    }
}
