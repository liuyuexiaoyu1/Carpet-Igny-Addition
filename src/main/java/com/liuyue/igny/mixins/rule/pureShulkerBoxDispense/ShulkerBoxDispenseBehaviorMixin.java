package com.liuyue.igny.mixins.rule.pureShulkerBoxDispense;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShulkerBoxDispenseBehavior.class)
public abstract class ShulkerBoxDispenseBehaviorMixin {
    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BlockItem;place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;"
            )
    )
    private InteractionResult dispenseFrom(BlockItem instance, BlockPlaceContext blockPlaceContext, Operation<InteractionResult> original, @Local(argsOnly = true) BlockSource blockSource, @Local(argsOnly = true) ItemStack itemStack) {
        if (IGNYSettings.pureShulkerBoxDispense) {
            ItemStack originalStack = blockPlaceContext.getItemInHand();
            originalStack.remove(DataComponents.CUSTOM_NAME);
            if (isColoredShulkerBox(itemStack)) {
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                Direction direction2 = blockSource.level().isEmptyBlock(blockPos.below()) ? direction : Direction.UP;
                ItemStack cleanStack = new ItemStack(Items.SHULKER_BOX);
                instance = (BlockItem) cleanStack.getItem();
                cleanStack.applyComponents(originalStack.getComponents());
                DirectionalPlaceContext newContext = new DirectionalPlaceContext(
                        blockPlaceContext.getLevel(),
                        blockPos,
                        direction,
                        cleanStack,
                        direction2
                );
                InteractionResult result = original.call(instance, newContext);
                if (result.consumesAction()) {
                    originalStack.shrink(1);
                }
                return result;
            }
        }
        return original.call(instance, blockPlaceContext);
    }

    @Unique
    private static boolean isColoredShulkerBox(ItemStack itemStack) {
        return itemStack.is(Items.WHITE_SHULKER_BOX) ||
                itemStack.is(Items.ORANGE_SHULKER_BOX) ||
                itemStack.is(Items.MAGENTA_SHULKER_BOX) ||
                itemStack.is(Items.LIGHT_BLUE_SHULKER_BOX) ||
                itemStack.is(Items.YELLOW_SHULKER_BOX) ||
                itemStack.is(Items.LIME_SHULKER_BOX) ||
                itemStack.is(Items.PINK_SHULKER_BOX) ||
                itemStack.is(Items.GRAY_SHULKER_BOX) ||
                itemStack.is(Items.LIGHT_GRAY_SHULKER_BOX) ||
                itemStack.is(Items.CYAN_SHULKER_BOX) ||
                itemStack.is(Items.PURPLE_SHULKER_BOX) ||
                itemStack.is(Items.BLUE_SHULKER_BOX) ||
                itemStack.is(Items.BROWN_SHULKER_BOX) ||
                itemStack.is(Items.GREEN_SHULKER_BOX) ||
                itemStack.is(Items.RED_SHULKER_BOX) ||
                itemStack.is(Items.BLACK_SHULKER_BOX);
    }
}