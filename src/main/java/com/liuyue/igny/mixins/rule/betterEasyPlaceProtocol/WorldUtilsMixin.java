package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.ItemStackProtocolDataAdapter;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.container.LitematicaBlockStateContainer;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;
import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement;
import fi.dy.masa.litematica.util.SchematicUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
//#if MC <= 12006
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.litematica.world.SchematicWorldHandler;

import java.util.List;
import java.util.Map;

import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.encodeProtocolValueToHitVecZ;

@Restriction(
        require = {
                @Condition(value = "litematica", versionPredicates = ">=0.14")
        }
)
@Mixin(WorldUtils.class)
public abstract class WorldUtilsMixin {
    //#if MC <= 12006
    //$$ @Shadow private static void cacheEasyPlacePosition(BlockPos pos) {}
    //#endif

    //#if MC >= 26.2
    //$$ @Definition(id = "applyCarpetProtocolHitVec", method = "Lfi/dy/masa/litematica/util/EasyPlaceUtils;applyCarpetProtocolHitVec(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
    //#else
    @Definition(id = "applyCarpetProtocolHitVec", method = "Lfi/dy/masa/litematica/util/WorldUtils;applyCarpetProtocolHitVec(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
    //#endif
    @Expression("? = applyCarpetProtocolHitVec(?, ?, ?)")
    @ModifyVariable(
            method = "doEasyPlaceAction(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
            name = "hitPos",
            require = 0
    )
    private static Vec3 igny_replaceHitPos(Vec3 hitPos, @Local(name = "pos") BlockPos pos, @Local(name = "world") Level world, @Local(name = "stateSchematic") BlockState stateSchematic) {
        return encodeHitPosItemData(hitPos, pos, world, stateSchematic);
    }

    //#if MC >= 26.2
    //$$ @Definition(id = "applyPlacementProtocolV3", method = "Lfi/dy/masa/litematica/util/EasyPlaceUtils;applyPlacementProtocolV3(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
    //#else
    @Definition(id = "applyPlacementProtocolV3", method = "Lfi/dy/masa/litematica/util/WorldUtils;applyPlacementProtocolV3(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
    //#endif
    @Expression("? = applyPlacementProtocolV3(?, ?, ?)")
    @ModifyVariable(
            method = "doEasyPlaceAction(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
            name = "hitPos",
            require = 0
    )
    private static Vec3 igny_replaceHitPosV3(Vec3 hitPos, @Local(name = "pos") BlockPos pos, @Local(name = "world") Level world, @Local(name = "stateSchematic") BlockState stateSchematic) {
        return encodeHitPosItemData(hitPos, pos, world, stateSchematic);
    }

    @Unique
    private static Vec3 encodeHitPosItemData(Vec3 hitPos, BlockPos pos, Level world, BlockState stateSchematic) {
        if (!BetterEasyPlaceProtocolHandler.isRuleEnabled()) {
            return hitPos;
        }
        Block block = stateSchematic.getBlock();
        com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter adapter =
                BetterEasyPlaceProtocolHandler.getAdapter(block);
        if (!(adapter instanceof ItemStackProtocolDataAdapter itemStackAdapter)) {
            if (adapter != null) {
                int added = adapter.igny$toProtocolValue(0, stateSchematic);
                if (block instanceof PistonBaseBlock && stateSchematic.getValue(PistonBaseBlock.EXTENDED)) {
                    Level schematicWorld = SchematicWorldHandler.getSchematicWorld();
                    if (schematicWorld != null) {
                        BlockState headState = schematicWorld.getBlockState(pos.relative(stateSchematic.getValue(PistonBaseBlock.FACING)));
                        if (headState.getBlock() instanceof PistonHeadBlock) {
                            added |= 0b1_0000_0000;
                        }
                    }
                }
                return encodeProtocolValueToHitVecZ(added, hitPos);
            }
            return hitPos;
        }
        int protocolAdditionValue = adapter.igny$toProtocolValue(0, stateSchematic);
        int attributesValue = 0;
        //#if MC >= 12005
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
        if (protocolAdditionValue == 0) {
            return hitPos;
        }
        return encodeProtocolValueToHitVecZ(protocolAdditionValue, hitPos);
    }
    @Unique
    private static @Nullable BlockEntity getSchematicWorldBlockEntity(BlockPos pos) {
        Level schematicWorld = SchematicWorldHandler.getSchematicWorld();
        if (schematicWorld == null) {
            return null;
        }
        return schematicWorld.getBlockEntity(pos);
    }

    @Unique
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

    @Definition(id = "getEffectiveProtocolVersion", method = "Lfi/dy/masa/litematica/util/PlacementHandler;getEffectiveProtocolVersion()Lfi/dy/masa/litematica/util/EasyPlaceProtocol;")
    @Expression("? = getEffectiveProtocolVersion()")
    @Inject(
            method = "doEasyPlaceAction(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
            cancellable = true,
            require = 0
    )
    private static void igny_processMultiStageBlock(
            Minecraft mc, CallbackInfoReturnable<InteractionResult> cir,
            @Local(name = "hand") InteractionHand hand,
            @Local(name = "stateClient") BlockState stateClient,
            @Local(name = "pos") BlockPos pos,
            @Local(name = "stateSchematic") BlockState stateSchematic,
            @Local(name = "hitPos") Vec3 hitPos
    ) {
        if (!BetterEasyPlaceProtocolHandler.isRuleEnabled()) {
            return;
        }
        if (!(BetterEasyPlaceProtocolHandler.getAdapter(stateSchematic.getBlock()) instanceof MultiStageBlockProtocolStateAdapter multiStageAdapter)) {
            return;
        }
        MultiStageBlockProtocolStateAdapter.LoopContext ctx = new MultiStageBlockProtocolStateAdapter.LoopContext();
        ctx.stateSchematic = stateSchematic;
        ctx.stateClient = stateClient;
        multiStageAdapter.igny$setLoopCount(ctx);
        int stackCount = Integer.MAX_VALUE;
        if (!mc.player.getAbilities().instabuild) {
            if (hand == InteractionHand.MAIN_HAND) {
                stackCount = mc.player.getMainHandItem().getCount();
            } else if (hand == InteractionHand.OFF_HAND) {
                stackCount = mc.player.getOffhandItem().getCount();
            } else {
                stackCount = 0;
            }
        }
        int loopCount = Math.min(stackCount, ctx.loopCount);
        for (int i = 0; i < loopCount; ++i) {
            ctx.loopIndex = i;
            int protocolRawValue = multiStageAdapter.igny$toProtocolValueLoop(ctx);
            Vec3 protocolHitVec = encodeProtocolValueToHitVecZ(protocolRawValue, hitPos);

            BlockHitResult hitResult = new BlockHitResult(protocolHitVec, Direction.UP, pos, false);
            mc.gameMode.useItemOn(mc.player, hand, hitResult);
            ctx.stateClient = mc.level.getBlockState(pos);
        }
        //#if MC <= 12006
        //$$ cacheEasyPlacePosition(pos);
        //#else
        EasyPlaceUtilsInvoker.invokeCacheEasyPlacePosition(pos);
        //#endif
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(
            method = "doEasyPlaceAction(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Z)Lnet/minecraft/world/phys/BlockHitResult;"),
            cancellable = true,
            require = 0
    )
    private static void igny_checkCoralFanAttachment(
            Minecraft mc, CallbackInfoReturnable<InteractionResult> cir,
            @Local(name = "pos") BlockPos pos,
            @Local(name = "stateSchematic") BlockState stateSchematic
    ) {
        if (!BetterEasyPlaceProtocolHandler.isRuleEnabled()) return;
        if (!(stateSchematic.getBlock() instanceof BaseCoralWallFanBlock)) return;
        Direction facing = stateSchematic.getValue(BaseCoralWallFanBlock.FACING).getOpposite();
        BlockPos attachPos = pos.relative(facing);
        if (!mc.level.getBlockState(attachPos).isFaceSturdy(mc.level, attachPos, facing.getOpposite())) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
