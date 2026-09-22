package com.liuyue.igny.mixins.rule.pureShulkerBoxDispense;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents; //?>= 1.20.5
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ShulkerBoxDispenseBehavior.class, priority = 999)
public abstract class ShulkerBoxDispenseBehaviorMixin {
    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BlockItem;place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;"
            )
    )
    private InteractionResult BlockItem(BlockItem instance, BlockPlaceContext blockPlaceContext, Operation<InteractionResult> original, @Local(argsOnly = true) ItemStack itemStack, @Local(ordinal = 0) Direction direction, @Local BlockPos blockPos, @Local(ordinal = 1) Direction direction2) {
        if (IGNYSettings.PURE_SHULKER_BOX_DISPENSE.value()) {
            ItemStack originalStack = blockPlaceContext.getItemInHand();
            originalStack.remove(DataComponents.CUSTOM_NAME); //#replace < 1.20.5 ? originalStack.resetHoverName();
            if (((BlockItem) itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock && !itemStack.is(Items.SHULKER_BOX)) {
                ItemStack cleanStack = new ItemStack(Items.SHULKER_BOX);
                cleanStack.setCount(originalStack.getCount());
                instance = (BlockItem) cleanStack.getItem();
                cleanStack.applyComponents(originalStack.getComponents()); //#replace < 1.20.5 ? if (itemStack.getTag() != null) cleanStack.setTag(itemStack.getTag().copy());
                DirectionalPlaceContext newContext = new DirectionalPlaceContext(
                        blockPlaceContext.getLevel(),
                        blockPos,
                        direction,
                        cleanStack,
                        direction2
                );
                InteractionResult result = original.call(instance, newContext);
                if (result.consumesAction()) {
                    originalStack.shrink(originalStack.getCount() - cleanStack.getCount());
                }
                return result;
            }
        }
        return original.call(instance, blockPlaceContext);
    }
}