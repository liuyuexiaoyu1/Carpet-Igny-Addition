package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents; //#replace < 1.20.5 ? import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//?>= 1.21.9 ? import net.minecraft.core.Direction;

import java.util.OptionalInt;

@Mixin(EnderChestBlock.class)
public class EnderChestBlockMixin extends Block {
    public EnderChestBlockMixin(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("all")
    @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;")) //#replace < 1.20.5 ? @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"))
    private OptionalInt openMenu(Player instance, MenuProvider menu, Operation<OptionalInt> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos blockPos, @Local PlayerEnderChestContainer chestContainer) {
        BlockEntity be = level.getBlockEntity(blockPos);
        if (!(be instanceof EnderChestBlockEntity chest) || !chest.components().has(DataComponents.CUSTOM_NAME)) //#replace < 1.20.5 ? if (!(be instanceof EnderChestBlockEntity chest) || !be.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        {
            return original.call(instance, menu);
        }
        ((ViewingChest) instance).igny$setContextChest(chest);
        String name = be.components().get(DataComponents.CUSTOM_NAME).getString(); //#replace < 1.20.5 ? String name = Component.Serializer.fromJson(be.saveWithFullMetadata().getString("CustomName")).getString();
        PlayerEnderChestContainer container = LinkedContainerManager.isRuleEnabled() ? LinkedContainerManager.get(name) : chestContainer;
        int size = container.getContainerSize();
        int rows = container.getContainerSize() / 9;
        MenuProvider provider = new SimpleMenuProvider((i, inventory, playerx) -> {
            switch (rows) {
                case 6: return ChestMenu.sixRows(i, inventory, container);
                case 3: return ChestMenu.threeRows(i, inventory, container);
                default: return null;
            }
        }, Component.literal(name));
        return original.call(instance, provider);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) //#replace >= 1.21.9 ? public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction)
    {
        if (!LinkedContainerManager.isRuleFully()) return 0;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Container container) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
        }
        return 0;
    }

    @WrapOperation(method = "getTicker", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", opcode = Opcodes.GETFIELD)) //#replace >= 1.21.9 ? @WrapOperation(method = "getTicker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean getTicker(Level instance, Operation<Boolean> original) {
        return true;
    }
}
