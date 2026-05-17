package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.helper.inventory.LinkedContainer;
import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
//#else
//$$ import net.minecraft.nbt.Tag;
//#endif
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//#if MC >= 12109
//$$ import net.minecraft.core.Direction;
//#endif

import java.util.OptionalInt;

@Mixin(EnderChestBlock.class)
public class EnderChestBlockMixin extends Block {
    public EnderChestBlockMixin(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("all")
    //#if MC >= 12005
    @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"))
    //#else
    //$$ @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"))
    //#endif
    private OptionalInt openMenu(Player instance, MenuProvider menu, Operation<OptionalInt> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos blockPos, @Local PlayerEnderChestContainer chestContainer) {
        BlockEntity be = level.getBlockEntity(blockPos);
        //#if MC < 12005
        //$$ if (!(be instanceof EnderChestBlockEntity chest) || !be.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        //#else
        if (!(be instanceof EnderChestBlockEntity chest) || !chest.components().has(DataComponents.CUSTOM_NAME))
        //#endif
        {
            return original.call(instance, menu);
        }
        ((ViewingChest) instance).igny$setContextChest(chest);
        //#if MC < 12005
        //$$ String name = Component.Serializer.fromJson(be.saveWithFullMetadata().getString("CustomName")).getString();
        //#else
        String name = be.components().get(DataComponents.CUSTOM_NAME).getString();
        //#endif
        LinkedContainer container = LinkedContainerManager.get(name);
        if (LinkedContainerManager.isRuleEnabled()) {
            return original.call(instance, new SimpleMenuProvider((i, inventory, playerx) ->
                    ChestMenu.threeRows(i, inventory, container), Component.literal(name))
            );
        } else {
            return original.call(instance, new SimpleMenuProvider((i, inventory, playerx) ->
                    ChestMenu.threeRows(i, inventory, chestContainer), Component.literal(name))
            );
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    //#if MC >= 12109
    //$$ public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction)
    //#else
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    //#endif
    {
        if (!LinkedContainerManager.isRuleFully()) return 0;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Container container) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
        }
        return 0;
    }
}
