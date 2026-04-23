package com.liuyue.igny.mixins.rule.renewableEndGatewayPortal;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//#if MC >= 26.2
//$$ import net.minecraft.world.entity.EntityTypes;
//#else
import net.minecraft.world.entity.EntityType;
//#endif
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12102
//$$ import net.minecraft.world.entity.EntitySpawnReason;
//#endif

import java.util.List;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
    @Inject(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canSurvive(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private static void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
        if (IGNYSettings.renewableEndGatewayPortal) {
            List<Direction> directions = List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
            boolean canPlacePortal = true;
            for (Direction direction : directions) {
                if (!level.getBlockState(pos.relative(direction)).is(Blocks.DRAGON_EGG)) {
                    canPlacePortal = false;
                    break;
                }
            }
            if (canPlacePortal) {
                level.setBlock(pos, Blocks.END_GATEWAY.defaultBlockState(), 3);
                //#if MC >= 12102
                //#if MC >= 26.2
                //$$ LightningBolt lightningBolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
                //#else
                //$$ LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
                //#endif
                //#else
                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
                //#endif
                if (lightningBolt != null) {
                    lightningBolt.moveTo(pos.getCenter());
                    level.addFreshEntity(lightningBolt);
                }
                ci.cancel();
            }
        }
    }
}
