package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.ItemStackProtocolDataAdapter;
//#if MC >= 12005
import net.minecraft.core.Holder;
//#endif
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
//#endif
//#if MC >= 12005 && MC < 12110
import net.minecraft.world.item.component.CustomData;
//#endif
//#if MC >= 12110
//$$ import net.minecraft.world.item.component.TypedEntityData;
//#if MC >= 26.2
//$$ import net.minecraft.world.level.block.entity.BlockEntityTypes;
//#else
//$$ import net.minecraft.world.level.block.entity.BlockEntityType;
//#endif
//#endif
//#if MC < 12005
//$$ import net.minecraft.nbt.CompoundTag;
//#endif

public class BeaconBlockProtocolAdapter implements BlockProtocolStateAdapter, ItemStackProtocolDataAdapter {
    public static final BeaconBlockProtocolAdapter INSTANCE = new BeaconBlockProtocolAdapter();

    public BeaconBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        return 0;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        return fromState;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }

    @Override
    public int igny$toProtocolValueAddition(ItemStack fromStack) {
        return 0;
    }

    @Override
    public @NotNull ItemStack igny$fromProtocolValueAddition(int extraProtocolValue, ItemStack fromStack) {
        int primaryId = (extraProtocolValue >>> 7) & 0x7F;
        int secondaryId = extraProtocolValue & 0x7F;
        if (primaryId == 0 && secondaryId == 0) {
            return fromStack;
        }

        ItemStack stackCopy = fromStack.copy();
        CompoundTag tag = getBlockEntityTag(stackCopy);
        if (tag == null) {
            tag = new CompoundTag();
        }
        if (tag.contains("primary_effect") || tag.contains("secondary_effect")) {
            return fromStack;
        }

        Registry<MobEffect> registry = BuiltInRegistries.MOB_EFFECT;
        if (primaryId != 0) {
            MobEffect primary = registry.byId(primaryId - 1);
            if (primary != null) {
                tag.putString("primary_effect", registry.getKey(primary).toString());
            }
        }
        if (secondaryId != 0) {
            MobEffect secondary = registry.byId(secondaryId - 1);
            if (secondary != null) {
                tag.putString("secondary_effect", registry.getKey(secondary).toString());
            }
        }
        return setBlockEntityTag(stackCopy, tag);
    }

    //#if MC >= 12005
    public static int encodeEffects(@Nullable Holder<MobEffect> primary, @Nullable Holder<MobEffect> secondary)
    //#else
    //$$ public static int encodeEffects(@Nullable MobEffect primary, @Nullable MobEffect secondary)
    //#endif
    {
        int p = 0;
        int s = 0;
        if (primary != null) {
            //#if MC >= 12005
            MobEffect effect = primary.value();
            //#else
            //$$ MobEffect effect = primary;
            //#endif
            p = BuiltInRegistries.MOB_EFFECT.getId(effect) + 1;
        }
        if (secondary != null) {
            //#if MC >= 12005
            MobEffect effect = secondary.value();
            //#else
            //$$ MobEffect effect = secondary;
            //#endif
            s = BuiltInRegistries.MOB_EFFECT.getId(effect) + 1;
        }
        return (p << 7) | s;
    }

    //#if MC < 12005
    //$$ private static @Nullable CompoundTag getBlockEntityTag(ItemStack stack) {
    //$$     return stack.getTagElement("BlockEntityTag");
    //$$ }
    //$$ private static ItemStack setBlockEntityTag(ItemStack stack, CompoundTag tag) {
    //$$     ItemStack stackCopy = stack.copy();
    //$$     CompoundTag beTag = stackCopy.getTagElement("BlockEntityTag");
    //$$     if (beTag == null) {
    //$$         beTag = tag;
    //$$         stackCopy.getOrCreateTag().put("BlockEntityTag", beTag);
    //$$     } else {
    //$$         beTag.putString("primary_effect", tag.getString("primary_effect"));
    //$$         beTag.putString("secondary_effect", tag.getString("secondary_effect"));
    //$$     }
    //$$     return stackCopy;
    //$$ }
    //#endif
    //#if MC >= 12005 && MC < 12110
    private static @Nullable CompoundTag getBlockEntityTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return data == null ? null : data.copyTag();
    }

    private static ItemStack setBlockEntityTag(ItemStack stack, CompoundTag tag) {
        ItemStack stackCopy = stack.copy();
        stackCopy.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
        return stackCopy;
    }
    //#endif
    //#if MC >= 12110
    //$$ private static @Nullable CompoundTag getBlockEntityTag(ItemStack stack) {
    //$$     TypedEntityData<?> data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
    //$$     return data == null ? null : data.copyTagWithoutId();
    //$$ }
    //$$ private static ItemStack setBlockEntityTag(ItemStack stack, CompoundTag tag) {
    //$$     ItemStack stackCopy = stack.copy();
    //#if MC >= 26.2
    //$$     stackCopy.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(BlockEntityTypes.BEACON, tag));
    //#else
    //$$     stackCopy.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(BlockEntityType.BEACON, tag));
    //#endif
    //$$     return stackCopy;
    //$$ }
    //#endif
}
