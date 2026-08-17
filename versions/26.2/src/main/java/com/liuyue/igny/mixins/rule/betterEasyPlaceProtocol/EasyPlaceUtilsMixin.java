package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.BlockProtocolStateAdapter;
import fi.dy.masa.litematica.util.EasyPlaceUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper.*;

@Mixin(EasyPlaceUtils.class)
public class EasyPlaceUtilsMixin {
    @Unique
    private static void igny_encodeProtocol(BlockPos pos, BlockState state, Vec3 hitVecIn, CallbackInfoReturnable<Vec3> cir) {
        if (!BetterEasyPlaceProtocolHandler.isRuleEnabled()) {
            return;
        }
        Block block = state.getBlock();
        int wallProtocolValue = 0;
        boolean isWallBlock = false;
        if (block.asItem() instanceof StandingAndWallBlockItem standingAndWallBlockItem) {
            isWallBlock = true;
            if (block.equals(((StandingAndWallBlockItemAccessor) standingAndWallBlockItem).igny$getWallBlock())) {
                Property<Direction> dir = EasyPlaceExtraProtocolHelper.getFirstDirectionProperty(state);
                if (dir != null) {
                    int facingIndex = state.getValue(dir).ordinal() - 2;
                    wallProtocolValue = ((facingIndex & 0b0000_0011) << 1);
                }

                wallProtocolValue |= 0b0000_0001;
            }
        }

        if (!(BetterEasyPlaceProtocolHandler.getAdapter(block) instanceof BlockProtocolStateAdapter adapter)) {
            if (isWallBlock) {
                cir.setReturnValue(encodeProtocolValueToHitVecX(wallProtocolValue, hitVecIn));
            }
            return;
        }

        if (adapter.igny$getProtocolType() == BlockProtocolStateAdapter.ProtocolType.REPLACE) {
            int protocolRawValue = adapter.igny$toProtocolValue(0, state);
            Vec3 returnValue;
            if (isWallBlock) {
                if ((wallProtocolValue & 0b0000_0001) == 0b0000_0001) {
                    protocolRawValue = (protocolRawValue << 3) | (wallProtocolValue & 0b0000_0111);
                } else {
                    protocolRawValue <<= 1;
                }
                returnValue = encodeProtocolValueToHitVecX(protocolRawValue, hitVecIn);
            } else {
                returnValue = encodeExtraProtocolValueToHitVecX(protocolRawValue, hitVecIn);
            }
            cir.setReturnValue(returnValue);
        } else {

            int protocolValue = addExtraProtocolBit(adapter.igny$toProtocolValue(0, state));
            if (block instanceof PistonBaseBlock && state.getValue(PistonBaseBlock.EXTENDED)) {
                Level schematicWorld = SchematicWorldHandler.getSchematicWorld();
                if (schematicWorld != null) {
                    BlockState headState = schematicWorld.getBlockState(pos.relative(state.getValue(PistonBaseBlock.FACING)));
                    if (headState.getBlock() instanceof PistonHeadBlock) {
                        protocolValue |= 0b1_0000_0000;
                    }
                }
            }
            cir.setReturnValue(encodeProtocolValueToHitVecX(protocolValue, hitVecIn));
        }
    }

    @Inject(
            method = "applyCarpetProtocolHitVec(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "HEAD"),
            cancellable = true,
            require = 0
    )
    private static void igny_replaceExtraProtocol(BlockPos pos, BlockState state, Vec3 hitVecIn, CallbackInfoReturnable<Vec3> cir) {
        igny_encodeProtocol(pos, state, hitVecIn, cir);
    }

    @Inject(
            method = "applyPlacementProtocolV3(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "HEAD"),
            cancellable = true,
            require = 0
    )
    private static void igny_replaceV3Protocol(BlockPos pos, BlockState state, Vec3 hitVecIn, CallbackInfoReturnable<Vec3> cir) {
        igny_encodeProtocol(pos, state, hitVecIn, cir);
    }
}
