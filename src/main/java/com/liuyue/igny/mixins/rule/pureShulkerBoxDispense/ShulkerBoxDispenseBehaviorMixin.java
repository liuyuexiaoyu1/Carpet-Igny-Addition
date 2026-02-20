package com.liuyue.igny.mixins.rule.pureShulkerBoxDispense;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
//#endif
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
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
    private InteractionResult dispenseFrom(BlockItem instance, BlockPlaceContext blockPlaceContext, Operation<InteractionResult> original, @Local(argsOnly = true) ItemStack itemStack, @Local(ordinal = 0) Direction direction, @Local BlockPos blockPos, @Local(ordinal = 1) Direction direction2) {
        if (IGNYSettings.pureShulkerBoxDispense) {
            ItemStack originalStack = blockPlaceContext.getItemInHand();
            //#if MC >= 12005
            originalStack.remove(DataComponents.CUSTOM_NAME);
            //#else
            //$$ originalStack.resetHoverName();
            //#endif
            if (isColoredShulkerBox(itemStack)) {
                ItemStack cleanStack = new ItemStack(Items.SHULKER_BOX);
                cleanStack.setCount(64);
                int originalCount = cleanStack.getCount();
                instance = (BlockItem) cleanStack.getItem();
                //#if MC >= 12005
                cleanStack.applyComponents(originalStack.getComponents());
                //#else
                //$$ if (itemStack.getTag() != null) cleanStack.setTag(itemStack.getTag().copy());
                //#endif
                DirectionalPlaceContext newContext = new DirectionalPlaceContext(
                        blockPlaceContext.getLevel(),
                        blockPos,
                        direction,
                        cleanStack,
                        direction2
                );
                InteractionResult result = original.call(instance, newContext);
                if (result.consumesAction()) {
                    originalStack.shrink(cleanStack.getCount() - originalCount);
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