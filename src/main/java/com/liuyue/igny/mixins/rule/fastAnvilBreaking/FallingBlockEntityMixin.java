package com.liuyue.igny.mixins.rule.fastAnvilBreaking;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Shadow
    private BlockState blockState;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;discard()V", ordinal = 3))
    private void tick(CallbackInfo ci, @Local BlockPos blockPos, @Local ServerLevel level) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        if (IGNYSettings.fastAnvilBreaking && this.blockState.is(BlockTags.ANVIL)) {
            BlockPos endPos = new BlockPos(blockPos.getX(), level.getMaxBuildHeight(), blockPos.getZ());
            List<FallingBlockEntity> fallingEntities = self.level().getEntitiesOfClass(
                    FallingBlockEntity.class,
                    new AABB(blockPos.getCenter(), endPos.getCenter())
            );
            for (FallingBlockEntity entity : fallingEntities) {
                if (entity.getBlockState().is(BlockTags.ANVIL)) {
                    //#if MC >= 12102
                    //$$ entity.spawnAtLocation(level, entity.getBlockState().getBlock());
                    //#else
                    entity.spawnAtLocation(entity.getBlockState().getBlock());
                    //#endif
                    entity.discard();
                }
            }
            for (int y = 1; blockPos.getY() + y <= level.getMaxBuildHeight(); y++) {
                BlockPos checkPos = blockPos.above(y);
                boolean foundSomething = false;
                BlockState upperState = level.getBlockState(checkPos);
                if (upperState.is(BlockTags.ANVIL)) {
                    level.destroyBlock(checkPos, true);
                    foundSomething = true;
                }
                if (!foundSomething && !FallingBlock.isFree(upperState)) {
                    break;
                }
            }
        }
    }
}
