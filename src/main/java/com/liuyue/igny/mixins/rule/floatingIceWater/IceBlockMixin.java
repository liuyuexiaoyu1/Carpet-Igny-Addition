package com.liuyue.igny.mixins.rule.floatingIceWater;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (IceBlock.class)
public class IceBlockMixin {
    @Inject(method = "playerDestroy",at = @At(value = "TAIL"))
    private void spawnWater(Level level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity, ItemStack itemStack, CallbackInfo ci) {
        if (IGNYSettings.floatingIceWater) {
            if (!(level.dimension() == Level.NETHER)) {
                level.setBlockAndUpdate(blockPos,
                        //#if MC <= 11904
                        Blocks.WATER.defaultBlockState()
                        //#else
                        //$$ IceBlock.meltsInto()
                        //#endif
                );
            }
        }
    }
}
