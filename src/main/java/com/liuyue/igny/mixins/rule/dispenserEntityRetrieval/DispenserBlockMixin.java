package com.liuyue.igny.mixins.rule.dispenserEntityRetrieval;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//#if MC >= 12002
//$$ import net.minecraft.core.dispenser.BlockSource;
//#else
//$$ import net.minecraft.core.BlockSourceImpl;
//#endif
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Inject(
            method = "dispenseFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/DispenseItemBehavior;dispense(Lnet/minecraft/core/dispenser/BlockSource;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true
    )
    private void dispenseFrom(ServerLevel serverLevel,
                              //#if MC >= 12002
                              BlockState blockState,
                              //#endif
                              BlockPos blockPos, CallbackInfo ci,
                              @Local DispenserBlockEntity blockEntity, @Local int i, @Local ItemStack itemStack
                              //#if MC >= 12002
                              //$$ ,@Local BlockSource blockSource
                              //#else
                              //$$ ,@Local BlockSourceImpl blockSource
                              //#endif
    ) {
        if (!IGNYSettings.DISPENSER_ENTITY_RETRIEVAL.value()) return;
        if (!itemStack.is(Items.BUCKET)) return;

        //#if MC <= 12001
        //$$ BlockState blockState = serverLevel.getBlockState(blockPos);
        //#endif
        Direction direction = blockState.getValue(DispenserBlock.FACING);
        BlockPos pos = blockPos.relative(direction);
        BlockState targetState = serverLevel.getBlockState(pos);
        if (!(targetState.getBlock() instanceof BucketPickup bucketPickup)) return;

        List<LivingEntity> entities = serverLevel.getEntitiesOfClass(
                LivingEntity.class, new AABB(pos),
                entity -> entity.isAlive() && entity instanceof Bucketable
        );
        if (entities.isEmpty()) return;

        LivingEntity fish = entities.getFirst();
        Bucketable bucketable = (Bucketable) fish;

        //#if MC >= 12002
        ItemStack waterBucket = bucketPickup.pickupBlock(null, serverLevel, pos, targetState);
        //#else
        //$$ ItemStack waterBucket = bucketPickup.pickupBlock(serverLevel, pos, targetState);
        //#endif
        if (waterBucket.isEmpty()) return;

        ItemStack fishBucket = bucketable.getBucketItemStack();
        bucketable.saveToBucketTag(fishBucket);
        fish.discard();
        serverLevel.gameEvent(null, GameEvent.FLUID_PICKUP, pos);

        serverLevel.levelEvent(1000, blockPos, 0);
        serverLevel.levelEvent(2000, blockPos, direction.get3DDataValue());

        itemStack.shrink(1);
        ItemStack result;
        if (itemStack.isEmpty()) {
            result = fishBucket;
        } else {
            //#if MC <= 12006
            //$$ int leftover = blockEntity.addItem(fishBucket);
            //$$ if (leftover < 0)
            //#else
            ItemStack leftover = blockEntity.insertItem(fishBucket);
            if (!leftover.isEmpty())
            //#endif
            {
                //#if MC >= 12101
                Vec3 position = blockPos.getCenter()
                        .add(0.7 * direction.getStepX(), 0.7 * direction.getStepY(), 0.7 * direction.getStepZ());
                DefaultDispenseItemBehavior.spawnItem(serverLevel, leftover, 6, direction, position);
                serverLevel.levelEvent(1000, blockPos, 0);
                serverLevel.levelEvent(2000, blockPos, direction.get3DDataValue());
                //#else
                //$$ DefaultDispenseItemBehavior.NOOP.dispense(blockSource, fishBucket);
                //#endif
            }
            result = itemStack;
        }
        blockEntity.setItem(i, result);
        ci.cancel();
    }
}
