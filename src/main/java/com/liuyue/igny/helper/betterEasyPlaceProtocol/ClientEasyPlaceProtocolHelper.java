package com.liuyue.igny.helper.betterEasyPlaceProtocol;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ClientEasyPlaceProtocolHelper {

    public static Vec3 encodeHitPosItemData(Vec3 hitPos, BlockPos pos, BlockState stateSchematic) {
        if (!BetterEasyPlaceProtocolHandler.isRuleEnabled()) {
            return hitPos;
        }
        Block block = stateSchematic.getBlock();
        Level world = SchematicWorldHandler.getSchematicWorld();
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
            added |= EasyPlaceExtraProtocolHelper.waterloggedBit(stateSchematic);
            if (added == 0) {
                return hitPos;
            }
            return EasyPlaceExtraProtocolHelper.encodeProtocolValueToHitVecZ(added, hitPos);
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
            if (blockEntity != null) {
                attributesValue = BetterEasyPlaceProtocolHandler.encodeBlockEntityProtocolAddition(blockEntity);
            }
            if (attributesValue == 0) {
                attributesValue = BetterEasyPlaceProtocolHandler.encodeBlockEntityNbtProtocolAddition(getSchematicBlockEntityNbt(pos));
            }
        }
        protocolAdditionValue |= attributesValue;
        protocolAdditionValue |= EasyPlaceExtraProtocolHelper.waterloggedBit(stateSchematic);
        if (protocolAdditionValue == 0) {
            return hitPos;
        }
        return EasyPlaceExtraProtocolHelper.encodeProtocolValueToHitVecZ(protocolAdditionValue, hitPos);
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