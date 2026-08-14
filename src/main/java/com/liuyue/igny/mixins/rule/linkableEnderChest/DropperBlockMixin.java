package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12002
//$$ import net.minecraft.core.dispenser.BlockSource;
//#else
//$$ import net.minecraft.core.BlockSourceImpl;
//#endif

@Mixin(DropperBlock.class)
public class DropperBlockMixin {
    @Shadow
    @Final
    private static DispenseItemBehavior DISPENSE_BEHAVIOUR;

    @Inject(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getContainerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/Container;", shift = At.Shift.AFTER), cancellable = true)
    //#if MC >= 12002
    private void getContainerAt(ServerLevel level, BlockState state, BlockPos pos, CallbackInfo ci, @Local(ordinal = 0) ItemStack itemStack, @Local BlockSource blockSource, @Local int slot, @Local DispenserBlockEntity blockEntity, @Local Direction direction)
    //#else
    //$$ private void getContainerAt(ServerLevel level, BlockPos pos, CallbackInfo ci, @Local(ordinal = 0) ItemStack itemStack, @Local BlockSourceImpl blockSource, @Local int slot, @Local DispenserBlockEntity blockEntity, @Local Direction direction)
    //#endif
    {
        if (level.getBlockEntity(pos.relative(direction)) instanceof LinkedEnderChest chest && !chest.carpet_Igny_Addition$isLinked()) {
            blockEntity.setItem(slot, DISPENSE_BEHAVIOUR.dispense(blockSource, itemStack));
            ci.cancel();
        }
    }
}
