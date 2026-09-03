package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.liuyue.igny.helper.betterEasyPlaceProtocol.EasyPlaceExtraProtocolHelper;
import com.liuyue.igny.utils.interfaces.betterEasyPlaceProtocol.MultiStageBlockProtocolStateAdapter;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.litematica.util.WorldUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
//#if MC <= 12006
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        return EasyPlaceExtraProtocolHelper.encodeHitPosItemData(hitPos, pos, world, stateSchematic);
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
        return EasyPlaceExtraProtocolHelper.encodeHitPosItemData(hitPos, pos, world, stateSchematic);
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
            Vec3 protocolHitVec = EasyPlaceExtraProtocolHelper.encodeProtocolValueToHitVecZ(protocolRawValue, hitPos);

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
