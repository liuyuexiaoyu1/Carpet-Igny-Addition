package com.liuyue.igny.helper.betterEasyPlaceProtocol;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.ItemStackProtocolDataAdapter;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.container.LitematicaBlockStateContainer;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;
import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement;
import fi.dy.masa.litematica.util.SchematicUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class EasyPlaceExtraProtocolHelper {
    public static final int WATERLOGGED_BIT = 1 << 16;

    public static boolean isProtocol(double relativeHitDim) {
        return relativeHitDim >= (double) 2.0F;
    }

    public static boolean isExtraProtocol(int protocolValue) {
        return (protocolValue & 0b0000_1000) == 0b0000_1000;
    }

    public static double getRelativeHitX(Vec3 hitPos, BlockPos blockPos) {
        return hitPos.x - (double) blockPos.getX();
    }

    public static double getRelativeHitZ(Vec3 hitPos, BlockPos blockPos) {
        return hitPos.z - (double) blockPos.getZ();
    }

    public static int decodeProtocolValueFromHitDim(double relativeHitDim) {
        return ((int) relativeHitDim - 2) >>> 1;
    }

    public static double encodeProtocolValueToHitDim(double relativeHitDim, int protocolValue) {
        return relativeHitDim + (double) ((protocolValue << 1) + 2);
    }

    public static int extraProtocolValueToRawProtocolValue(int protocolValue) {
        return ((protocolValue & 0b1111_0000) >>> 1) | (protocolValue & 0b0000_0111);
    }

    public static int rawProtocolValueToExtraProtocolValue(int protocolValue) {
        return ((protocolValue & 0b0111_1000) << 1) | (protocolValue & 0b0000_0111) | 0b0000_1000;
    }

    public static int addExtraProtocolBit(int protocolValue) {
        return protocolValue | 0b0000_1000;
    }

    public static int removeExtraProtocolBit(int protocolValue) {
        return protocolValue & ~((int) 0b0000_1000);
    }

    public static Vec3 encodeProtocolValueToHitVecZ(int protocolAdditionValue, Vec3 hitVec) {
        return new Vec3(hitVec.x, hitVec.y, encodeProtocolValueToHitDim(hitVec.z, protocolAdditionValue));
    }

    public static @Nullable Property<Direction> getFirstDirectionProperty(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (Direction.class.isAssignableFrom(property.getValueClass())) {
                @SuppressWarnings("unchecked")
                Property<Direction> directionProperty = (Property<Direction>) property;
                return directionProperty;
            }
        }
        return null;
    }

    public static Vec3 encodeHitPosItemData(Vec3 hitPos, BlockPos pos, Level world, BlockState stateSchematic) {
        if (!BetterEasyPlaceProtocolHandler.isRuleEnabled()) {
            return hitPos;
        }
        Block block = stateSchematic.getBlock();
        com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter adapter =
                BetterEasyPlaceProtocolHandler.getAdapter(block);
        if (!(adapter instanceof ItemStackProtocolDataAdapter itemStackAdapter)) {
            int added = 0;
            if (adapter != null) {
                added = adapter.igny$toProtocolValue(0, stateSchematic);
                if (block instanceof PistonBaseBlock && stateSchematic.getValue(PistonBaseBlock.EXTENDED)) {
                    Level schematicWorld = SchematicWorldHandler.getSchematicWorld();
                    if (schematicWorld != null) {
                        BlockState headState = schematicWorld.getBlockState(pos.relative(stateSchematic.getValue(PistonBaseBlock.FACING)));
                        if (headState.getBlock() instanceof PistonHeadBlock) {
                            added |= 0b1_0000_0000;
                        }
                    }
                }
            }
            added |= waterloggedBit(stateSchematic);
            if (added == 0) {
                return hitPos;
            }
            return encodeProtocolValueToHitVecZ(added, hitPos);
        }
        int protocolAdditionValue = adapter.igny$toProtocolValue(0, stateSchematic);
        int attributesValue = 0;
        //#if MC >= 12001
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            net.minecraft.world.item.ItemStack handStack = mc.player.getMainHandItem();
            if (handStack.isEmpty()) {
                handStack = mc.player.getOffhandItem();
            }
            attributesValue = itemStackAdapter.igny$toProtocolValueAddition(handStack);
        }
        //#endif
        if (attributesValue == 0) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity == null) {
                blockEntity = getSchematicWorldBlockEntity(pos);
            }
            if (blockEntity != null) {
                attributesValue = BetterEasyPlaceProtocolHandler.encodeBlockEntityProtocolAddition(blockEntity);
            }
            if (attributesValue == 0) {
                attributesValue = BetterEasyPlaceProtocolHandler.encodeBlockEntityNbtProtocolAddition(getSchematicBlockEntityNbt(pos));
            }
        }
        protocolAdditionValue |= attributesValue;
        protocolAdditionValue |= waterloggedBit(stateSchematic);
        if (protocolAdditionValue == 0) {
            return hitPos;
        }
        return encodeProtocolValueToHitVecZ(protocolAdditionValue, hitPos);
    }

    private static int waterloggedBit(BlockState state) {
        if (!IGNYSettings.EASY_PLACE_CAN_PLACE_WATERLOGGED_BLOCK.value()) {
            return 0;
        }
        if (!(state.getBlock() instanceof SimpleWaterloggedBlock)) {
            return 0;
        }
        if (!state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            return 0;
        }
        return state.getValue(BlockStateProperties.WATERLOGGED)
                ? EasyPlaceExtraProtocolHelper.WATERLOGGED_BIT : 0;
    }

    private static @Nullable BlockEntity getSchematicWorldBlockEntity(BlockPos pos) {
        Level schematicWorld = SchematicWorldHandler.getSchematicWorld();
        if (schematicWorld == null) {
            return null;
        }
        return schematicWorld.getBlockEntity(pos);
    }

    private static @Nullable CompoundTag getSchematicBlockEntityNbt(BlockPos pos) {
        try {
            List<SchematicPlacementManager.PlacementPart> parts = DataManager.getSchematicPlacementManager().getAllPlacementsTouchingChunk(pos);
            if (parts.isEmpty()) {
                return null;
            }
            for (SchematicPlacementManager.PlacementPart part : parts) {
                SchematicPlacement schematicPlacement = part.getPlacement();
                String regionName = part.getSubRegionName();
                if (schematicPlacement == null || regionName == null) {
                    continue;
                }
                SubRegionPlacement placement = schematicPlacement.getRelativeSubRegionPlacement(regionName);
                if (placement == null || !placement.isEnabled()) {
                    continue;
                }
                LitematicaSchematic schematic = schematicPlacement.getSchematic();
                if (schematic == null) {
                    continue;
                }
                LitematicaBlockStateContainer container = schematic.getSubRegionContainer(regionName);
                Map<BlockPos, CompoundTag> blockEntityMap = schematic.getBlockEntityMapForRegion(regionName);
                if (container == null || blockEntityMap == null || blockEntityMap.isEmpty()) {
                    continue;
                }
                BlockPos schematicPos = SchematicUtils.getSchematicContainerPositionFromWorldPosition(
                        pos, schematic, regionName, schematicPlacement, placement, container);
                if (schematicPos != null) {
                    CompoundTag nbt = blockEntityMap.get(schematicPos);
                    if (nbt != null) {
                        return nbt;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}