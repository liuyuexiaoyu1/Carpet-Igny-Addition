package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

//#if MC >= 12003

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.ItemStackProtocolDataAdapter;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
//$$ import net.minecraft.world.level.block.entity.BlockEntityType;
//#endif
//#if MC < 12005
//$$ import net.minecraft.nbt.CompoundTag;
//#endif

public class CrafterBlockProtocolAdapter implements BlockProtocolStateAdapter, ItemStackProtocolDataAdapter {
    public static final CrafterBlockProtocolAdapter INSTANCE = new CrafterBlockProtocolAdapter();

    public CrafterBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        int orientationOrdinal = fromState.getValue(BlockStateProperties.ORIENTATION).ordinal();
        return orientationOrdinal & 0b0000_1111;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        
        int orientationOrdinal = (extraProtocolValue & 0b0000_1111) % 12;
        return fromState.setValue(BlockStateProperties.ORIENTATION, FrontAndTop.values()[orientationOrdinal]);
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.REPLACE;
    }

    @Override
    public int igny$toProtocolValueAddition(ItemStack fromStack) {
        CompoundTag tagBlockEntity = getBlockEntityTag(fromStack);
        if (tagBlockEntity == null) {
            return 0;
        }

        int[] disSlots = getDisabledSlots(tagBlockEntity);
        int bits = 0;
        int mask = 1;
        for (int slotIdx : disSlots) {
            if (slotIdx > -1 && slotIdx < 9) {
                bits |= (mask << slotIdx);
            }
        }
        return bits & 0b0001_1111_1111;
    }

    @Override
    public @NotNull ItemStack igny$fromProtocolValueAddition(int extraProtocolValue, ItemStack fromStack) {
        int disCount = Integer.bitCount(extraProtocolValue & 0b0001_1111_1111);
        if (disCount == 0) {
            return fromStack;
        }

        int[] disSlots = new int[disCount];
        int slotIdx = 0;
        int mask = 1;
        for (int i = 0; i < 9; ++i) {
            if ((extraProtocolValue & mask) == mask) {
                disSlots[slotIdx++] = i;
            }
            mask <<= 1;
        }

        
        CompoundTag tagBlockEntity = getBlockEntityTag(fromStack);
        if (tagBlockEntity != null && tagBlockEntity.contains("disabled_slots")) {
            return fromStack;
        }
        if (tagBlockEntity == null) {
            tagBlockEntity = new CompoundTag();
        }
        tagBlockEntity.putIntArray("disabled_slots", disSlots);
        return setBlockEntityTag(fromStack, tagBlockEntity);
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
    //$$         beTag.putIntArray("disabled_slots", tag.getIntArray("disabled_slots"));
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
    //$$     stackCopy.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(BlockEntityType.CRAFTER, tag));
    //$$     return stackCopy;
    //$$ }
    //#endif

    
    //#if MC >= 12105
    //$$ private static int[] getDisabledSlots(CompoundTag tag) {
    //$$     if (!tag.contains("disabled_slots")) {
    //$$         return new int[0];
    //$$     }
    //$$     return tag.getIntArray("disabled_slots").orElseGet(() -> new int[0]);
    //$$ }
    //#else
    private static int[] getDisabledSlots(CompoundTag tag) {
        if (!tag.contains("disabled_slots", 11)) {
            return new int[0];
        }
        return tag.getIntArray("disabled_slots");
    }
    //#endif
}
//#endif