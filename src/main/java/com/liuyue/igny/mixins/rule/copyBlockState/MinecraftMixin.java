package com.liuyue.igny.mixins.rule.copyBlockState;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BlockItemStateProperties;
//#else
//$$ import net.minecraft.nbt.CompoundTag;
//#endif
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public HitResult hitResult;

    @WrapOperation(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack pickBlock(Block instance, LevelReader levelReader, BlockPos pos, BlockState state, Operation<ItemStack> original) {
        ItemStack itemStack = original.call(instance, levelReader, pos, state);
        if (IGNYSettings.COPY_BLOCK_STATE.value()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
                if (this.hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult) this.hitResult).getBlockPos();
                    if (player.isShiftKeyDown() && Screen.hasControlDown()) {
                        setBlockStateData(itemStack, player.level().getBlockState(blockPos));
                    }
                }
            }
        }
        return itemStack;
    }

    @Unique
    private static void setBlockStateData(ItemStack stack, BlockState state) {
        //#if MC >= 12006
        Map<String, String> map = new HashMap<>();

        for (Property<?> property : state.getProperties()) {
            setPropertyToMap(state, (Property<?>) property, map);
        }

        BlockItemStateProperties component = new BlockItemStateProperties(map);
        stack.set(DataComponents.BLOCK_STATE, component);
        //#else
        /*$$CompoundTag nbt = new CompoundTag();
        state.getValues().forEach((property, value) -> {
            nbt.putString(property.getName(), value.toString());
        });
        stack.getOrCreateTag().put("BlockStateTag", nbt);$$*/
        //#endif

    }

    @Unique
    private static <T extends Comparable<T>> void setPropertyToMap(BlockState state, Property<T> property, Map<String, String> map) {
        T value = state.getValue(property);
        map.put(property.getName(), property.getName(value));
    }
}
