package com.liuyue.igny.helper.betterEasyPlaceProtocol.adapter;

import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.ItemStackProtocolDataAdapter;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeilingHangingSignBlockProtocolAdapter implements BlockProtocolStateAdapter, ItemStackProtocolDataAdapter {
    public static final CeilingHangingSignBlockProtocolAdapter INSTANCE = new CeilingHangingSignBlockProtocolAdapter();

    private static final int BIT_GLOWING = 0b10_0000;
    private static final int BIT_COLOR_MASK = 0b1111 << 6;
    private static final int BIT_COLOR_SHIFT = 6;
    private static final int BIT_WAXED = 0b100_0000_0000;

    public CeilingHangingSignBlockProtocolAdapter() {
    }

    @Override
    public int igny$toProtocolValue(int protocolValue, BlockState fromState) {
        if (fromState.getBlock() instanceof CeilingHangingSignBlock) {
            int rotation = fromState.getValue(CeilingHangingSignBlock.ROTATION);
            boolean isAttached = fromState.getValue(CeilingHangingSignBlock.ATTACHED);
            return protocolValue
                    | (rotation & 0b0000_1111)
                    | (isAttached ? 0b0001_0000 : 0b0000_0000);
        }
        if (fromState.getBlock() instanceof WallHangingSignBlock) {
            int facing = fromState.getValue(WallHangingSignBlock.FACING).get3DDataValue();
            return protocolValue | (facing & 0b0000_0111);
        }
        return protocolValue;
    }

    @Override
    public @Nullable BlockState igny$fromProtocolValue(int extraProtocolValue, BlockState fromState, BlockPlaceContext context) {
        if (fromState.getBlock() instanceof CeilingHangingSignBlock) {
            int rotation = extraProtocolValue & 0b0000_1111;
            boolean isAttached = (extraProtocolValue & 0b0001_0000) == 0b0001_0000;
            return fromState
                    .setValue(CeilingHangingSignBlock.ROTATION, rotation)
                    .setValue(CeilingHangingSignBlock.ATTACHED, isAttached);
        }
        if (fromState.getBlock() instanceof WallHangingSignBlock) {
            int facingData = extraProtocolValue & 0b0000_0111;
            Direction[] dirs = Direction.values();
            Direction facing = facingData >= 0 && facingData < dirs.length ? dirs[facingData] : Direction.NORTH;
            return fromState.setValue(WallHangingSignBlock.FACING, facing);
        }
        return fromState;
    }

    @Override
    public @NotNull ProtocolType igny$getProtocolType() {
        return ProtocolType.ADDED;
    }

    @Override
    public int igny$toProtocolValueAddition(ItemStack fromStack) {
        int attributes = 0;
        //#if MC >= 12005
        CompoundTag tag = getBlockEntityTag(fromStack);
        if (tag == null) {
            return 0;
        }
        //#if MC >= 12105
        //$$ CompoundTag frontText = tag.getCompound("front_text").orElse(null);
        //$$ if (frontText != null && !frontText.isEmpty()) {
        //$$     if (frontText.getBoolean("has_glowing_text").orElse(false)) attributes |= BIT_GLOWING;
        //$$     String colorName = frontText.getString("color").orElse("");
        //$$     for (DyeColor c : DyeColor.values()) {
        //$$         if (c.getName().equals(colorName)) {
        //$$             attributes |= (c.ordinal() & 0b1111) << BIT_COLOR_SHIFT;
        //$$             break;
        //$$         }
        //$$     }
        //$$ }
        //$$ if (tag.getBoolean("is_waxed").orElse(false)) attributes |= BIT_WAXED;
        //#else
        CompoundTag frontText = tag.getCompound("front_text");
        if (frontText != null && !frontText.isEmpty()) {
            if (frontText.getBoolean("has_glowing_text")) attributes |= BIT_GLOWING;
            String colorName = frontText.getString("color");
            for (DyeColor c : DyeColor.values()) {
                if (c.getName().equals(colorName)) {
                    attributes |= (c.ordinal() & 0b1111) << BIT_COLOR_SHIFT;
                    break;
                }
            }
        }
        if (tag.getBoolean("is_waxed")) attributes |= BIT_WAXED;
        //#endif
        //#endif
        return attributes;
    }

    @Override
    public @NotNull ItemStack igny$fromProtocolValueAddition(int extraProtocolValue, ItemStack fromStack) {
        ItemStack stackCopy = fromStack.copy();

        boolean glowing = (extraProtocolValue & BIT_GLOWING) != 0;
        int colorOrdinal = (extraProtocolValue & BIT_COLOR_MASK) >>> BIT_COLOR_SHIFT;

        DyeColor[] colors = DyeColor.values();
        DyeColor color = colorOrdinal < colors.length
                ? colors[colorOrdinal]
                : DyeColor.BLACK;

        //#if MC >= 12005
        CompoundTag tag = getBlockEntityTag(stackCopy);
        if (tag == null) {
            tag = new CompoundTag();
        }
        applySignTextProperties(tag, "front_text", color, glowing);
        applySignTextProperties(tag, "back_text", color, glowing);
        return setBlockEntityTag(stackCopy, tag);
        //#else
        //$$ CompoundTag tag = stackCopy.getTagElement("BlockEntityTag");
        //$$ if (tag == null) {
        //$$     return fromStack;
        //$$ }
        //$$ applySignTextProperties(tag, "front_text", color, glowing);
        //$$ applySignTextProperties(tag, "back_text", color, glowing);
        //$$ stackCopy.getOrCreateTag().put("BlockEntityTag", tag);
        //$$ return stackCopy;
        //#endif
    }

    //#if MC >= 12005
    private static void applySignTextProperties(CompoundTag tag, String key, DyeColor color, boolean glowing) {
        //#if MC >= 12105
        //$$ CompoundTag text = tag.getCompound(key).orElse(null);
        //#else
        CompoundTag text = tag.getCompound(key);
        //#endif
        if (text == null) {
            text = new CompoundTag();
            tag.put(key, text);
        }
        if (!text.contains("messages")) {
            net.minecraft.nbt.ListTag messages = new net.minecraft.nbt.ListTag();
            for (int i = 0; i < 4; ++i) {
                messages.add(net.minecraft.nbt.StringTag.valueOf(""));
            }
            text.put("messages", messages);
        }
        text.putString("color", color.getName());
        text.putBoolean("has_glowing_text", glowing);
        tag.put(key, text);
    }
    //#else
    //$$ private static void applySignTextProperties(CompoundTag tag, String key, DyeColor color, boolean glowing) {
    //$$     CompoundTag text = tag.getCompound(key);
    //$$     if (text == null) {
    //$$         text = new CompoundTag();
    //$$         tag.put(key, text);
    //$$     }
    //$$     if (!text.contains("messages")) {
    //$$         net.minecraft.nbt.ListTag messages = new net.minecraft.nbt.ListTag();
    //$$         for (int i = 0; i < 4; ++i) {
    //$$             messages.add(net.minecraft.nbt.StringTag.valueOf(""));
    //$$         }
    //$$         text.put("messages", messages);
    //$$     }
    //$$     text.putString("color", color.getName());
    //$$     text.putBoolean("has_glowing_text", glowing);
    //$$     tag.put(key, text);
    //$$ }
    //#endif

    //#if MC >= 12005 && MC < 12110
    private static @Nullable CompoundTag getBlockEntityTag(ItemStack stack) {
        net.minecraft.world.item.component.CustomData data = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
        return data == null ? null : data.copyTag();
    }

    private static ItemStack setBlockEntityTag(ItemStack stack, CompoundTag tag) {
        ItemStack stackCopy = stack.copy();
        stackCopy.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, net.minecraft.world.item.component.CustomData.of(tag));
        return stackCopy;
    }
    //#endif
    //#if MC >= 12110
    //$$ private static @Nullable CompoundTag getBlockEntityTag(ItemStack stack) {
    //$$     net.minecraft.world.item.component.TypedEntityData<?> data = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
    //$$     return data == null ? null : data.copyTagWithoutId();
    //$$ }
    //$$
    //$$ private static ItemStack setBlockEntityTag(ItemStack stack, CompoundTag tag) {
    //$$     ItemStack stackCopy = stack.copy();
    //$$     stackCopy.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA,
    //#if MC >= 26.2
    //$$             net.minecraft.world.item.component.TypedEntityData.of(net.minecraft.world.level.block.entity.BlockEntityTypes.SIGN, tag));
    //#else
    //$$             net.minecraft.world.item.component.TypedEntityData.of(net.minecraft.world.level.block.entity.BlockEntityType.SIGN, tag));
    //#endif
    //$$     return stackCopy;
    //$$ }
    //#endif
}
