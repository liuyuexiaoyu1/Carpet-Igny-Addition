package com.liuyue.igny.mixins.features.logger.piston;

import carpet.CarpetSettings;
import com.liuyue.igny.helper.PistonResolveContext;
import com.liuyue.igny.logging.IGNYLoggerRegistry;
import com.liuyue.igny.utils.BlockUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {
    @Shadow
    @Final
    private boolean isSticky;

    @Inject(method = "triggerEvent", at = @At("HEAD"))
    private void onTriggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1, CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYLoggerRegistry.__piston || level.isClientSide()) return;
        carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
        if (logger == null || !logger.hasOnlineSubscribers()) return;
        Direction dir = state.getValue(PistonBaseBlock.FACING);
        if (b0 != 0 && !this.isSticky) {
            handleRetract(logger, level, pos, dir, false, null);
        }
    }

    @Inject(method = "triggerEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private void onTriggerEventRemoveBlock(BlockState state, Level level, BlockPos pos, int b0, int b1, CallbackInfoReturnable<Boolean> cir, @Local Direction direction) {
        if (!IGNYLoggerRegistry.__piston || level.isClientSide()) return;
        carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
        if (logger == null || !logger.hasOnlineSubscribers()) return;
        if (this.isSticky) handleRetract(logger, level, pos, direction, true, null);
    }

    @Unique
    private void handleRetract(carpet.logging.Logger logger, Level level, BlockPos pistonPos, Direction direction, boolean isSticky, @Nullable List<BlockPos> toPull) {
        Block block = isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON;
        boolean isEmpty = toPull == null || toPull.isEmpty();

        Component actionPart;
        if (isSticky && !isEmpty) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("igny.logger.piston.pulled.blocks", toPull.size()));
            for (BlockPos original : toPull) {
                String name = BlockUtils.getTranslatedName(level.getBlockState(original).getBlock()).getString();
                BlockPos newPos = original.relative(direction.getOpposite());
                lines.add(Component.literal("• " + name + " @ " + original.toShortString() + " → " + newPos.toShortString()));
            }
            MutableComponent hover = Component.empty();
            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) hover = hover.append(Component.literal("\n"));
                hover = hover.append(lines.get(i));
            }
            Component finalHover = hover;
            actionPart = Component.translatable("igny.logger.piston.pull")
                    .withStyle(s -> s.withColor(ChatFormatting.LIGHT_PURPLE)
                                    //#if MC >= 12105
                                    //$$ .withHoverEvent(new HoverEvent.ShowText(finalHover))
                                    //#else
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, finalHover))
                            //#endif
                    );
        } else {
            Component hover = Component.translatable("igny.logger.piston.no.blocks.moved");
            actionPart = Component.translatable(isSticky ? "igny.logger.piston.pull" : "igny.logger.piston.retract")
                    .withStyle(s -> s.withColor(ChatFormatting.GRAY)
                                    //#if MC >= 12105
                                    //$$ .withHoverEvent(new HoverEvent.ShowText(hover))
                                    //#else
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                            //#endif
                    );
        }

        Component full = Component.translatable("igny.logger.piston.action.performed", getPistonPartText(level, pistonPos, block), actionPart);
        logger.log(() -> new Component[]{full});
    }

    @Unique
    private Component getPistonPartText(Level level, BlockPos pistonPos, Block block) {
        String dimNameSpace = level.dimension().location().getNamespace();
        String dimPath = level.dimension().location().getPath();

        Component hoverText = Component.translatable("igny.logger.piston.hover.dimension_line", dimNameSpace + dimPath)
                .append("\n")
                .append(Component.translatable("igny.logger.piston.hover.position", pistonPos.toShortString()));

        return Component.literal("[")
                .append(BlockUtils.getTranslatedName(block))
                .append("] ")
                .withStyle(s -> s
                                //#if MC >= 12105
                                //$$ .withHoverEvent(new HoverEvent.ShowText(hoverText))
                                //$$ .withClickEvent(new ClickEvent.RunCommand("/igny highlight " + pistonPos.getX() + " " + pistonPos.getY() + " " + pistonPos.getZ()))
                                //#else
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND,
                                        "/igny highlight " + pistonPos.getX() + " " + pistonPos.getY() + " " + pistonPos.getZ()
                                ))
                        //#endif
                );
    }

    @Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"))
    private void pullBlocks(Level level, BlockPos blockPos, Direction direction, boolean extend, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) boolean isExtend, @Local List<BlockPos> list) {
        if (!IGNYLoggerRegistry.__piston || !(level instanceof ServerLevel)) return;
        carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
        if (logger == null || !logger.hasOnlineSubscribers()) return;

        if (!isExtend) {
            handleRetract(logger, level, blockPos, direction, this.isSticky, list);
        } else {
            Block block = level.getBlockState(blockPos).getBlock();
            boolean isEmpty = list.isEmpty();
            Component actionPart;
            if (!isEmpty) {
                List<Component> lines = new ArrayList<>();
                lines.add(Component.translatable("igny.logger.piston.pushed.blocks", list.size()));
                for (BlockPos original : list) {
                    String name = BlockUtils.getTranslatedName(level.getBlockState(original).getBlock()).getString();
                    BlockPos newPos = original.relative(direction);
                    lines.add(Component.literal("• " + name + " @ " + original.toShortString() + " → " + newPos.toShortString()));
                }
                MutableComponent hover = Component.empty();
                for (int i = 0; i < lines.size(); i++) {
                    if (i > 0) hover = hover.append(Component.literal("\n"));
                    hover = hover.append(lines.get(i));
                }
                MutableComponent finalHover = hover;
                actionPart = Component.translatable("igny.logger.piston.push")
                        .withStyle(s -> s.withColor(ChatFormatting.AQUA)
                                        //#if MC >= 12105
                                        //$$ .withHoverEvent(new HoverEvent.ShowText(finalHover))
                                        //#else
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, finalHover))
                                //#endif
                        );
            } else {
                Component hover = Component.translatable("igny.logger.piston.no.blocks.moved");
                actionPart = Component.translatable("igny.logger.piston.push")
                        .withStyle(s -> s.withColor(ChatFormatting.GRAY)
                                        //#if MC >= 12105
                                        //$$ .withHoverEvent(new HoverEvent.ShowText(hover))
                                        //#else
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                                //#endif
                        );
            }

            Component full = Component.translatable("igny.logger.piston.action.performed", getPistonPartText(level, blockPos, block), actionPart);
            logger.log(() -> new Component[]{full});
        }
    }

    @WrapOperation(
            method = "checkIfExtend",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z"
            )
    )
    private boolean wrapResolve(PistonStructureResolver instance, Operation<Boolean> original,
                                @Local(argsOnly = true) Level level,
                                @Local(argsOnly = true) BlockPos pos) {
        PistonResolveContext.startRecording();
        boolean result = original.call(instance);
        if (!IGNYLoggerRegistry.__piston || level.isClientSide()) {
            return result;
        }
        if (!result) {
            carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
            if (logger != null && logger.hasOnlineSubscribers()) {
                logPistonExtendFailure(logger, level, pos, true);
            }
        }
        PistonResolveContext.stopRecording();
        return result;
    }

    @WrapOperation(
            method = "moveBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z"
            )
    )
    private boolean wrapPullResolve(PistonStructureResolver instance, Operation<Boolean> original,
                                    @Local(argsOnly = true) Level level,
                                    @Local(argsOnly = true) BlockPos blockPos,
                                    @Local(argsOnly = true) boolean extend) {
        if (extend || !IGNYLoggerRegistry.__piston || level.isClientSide()) {
            return original.call(instance);
        }
        PistonResolveContext.startRecording();
        boolean result = original.call(instance);
        if (!result) {
            carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
            if (logger != null && logger.hasOnlineSubscribers()) {
                logPistonExtendFailure(logger, level, blockPos, false);
            }
        }
        PistonResolveContext.stopRecording();
        return result;
    }

    @Unique
    private void logPistonExtendFailure(carpet.logging.Logger logger, Level level, BlockPos pos, boolean isExtend) {
        PistonResolveContext.FailureReason reason = PistonResolveContext.getFailureReason();
        Component hoverContent;

        if (reason != null) {
            switch (reason.type) {
                case TOO_MANY_BLOCKS:
                    hoverContent = Component.translatable("igny.logger.piston.failure.too_many_blocks", CarpetSettings.pushLimit);
                    break;
                case UNPUSHABLE_BLOCK:
                    if (reason.blockPos != null) {
                        BlockState bs = level.getBlockState(reason.blockPos);
                        if (bs.is(Blocks.MOVING_PISTON)) return;
                        String name = BlockUtils.getDisplayName(bs);
                        Component blockInfo = Component.literal("• " + name + " @ " + reason.blockPos.toShortString());
                        hoverContent = Component.translatable("igny.logger.piston.failure.unpushable_block")
                                .append(Component.literal("\n"))
                                .append(blockInfo);
                    } else {
                        hoverContent = Component.translatable("igny.logger.piston.failure.unknown");
                    }
                    break;
                default:
                    hoverContent = Component.translatable("igny.logger.piston.failure.unknown");
                    break;
            }
        } else {
            hoverContent = Component.translatable("igny.logger.piston.failure.unknown");
        }

        Block block = isExtend ? level.getBlockState(pos).getBlock() : Blocks.STICKY_PISTON;

        Component dimensionNameFail;
        if (level instanceof ServerLevel serverLevel) {
            var dimKey = serverLevel.dimension().location();
            dimensionNameFail = Component.translatable(
                    "dimension." + dimKey.getNamespace() + "." + dimKey.getPath()
            );
        } else {
            dimensionNameFail = Component.literal("?");
        }
        Component hoverTextFail = Component.translatable("igny.logger.piston.hover.dimension_line", dimensionNameFail)
                .append("\n")
                .append(Component.translatable("igny.logger.piston.hover.position", pos.toShortString()));

        Component pistonPart = Component.literal("[")
                .append(BlockUtils.getTranslatedName(block))
                .append("] ")
                .withStyle(s -> s
                                //#if MC >= 12105
                                //$$ .withHoverEvent(new HoverEvent.ShowText(hoverTextFail))
                                //$$ .withClickEvent(new ClickEvent.RunCommand("/igny highlight " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))
                                //#else
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverTextFail))
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND,
                                        "/igny highlight " + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                                ))
                        //#endif
                );

        Component failPart = Component.translatable(isExtend ? "igny.logger.piston.push.failed" : "igny.logger.piston.pull.failed")
                .withStyle(s -> s.withColor(ChatFormatting.RED)
                                //#if MC >= 12105
                                //$$ .withHoverEvent(new HoverEvent.ShowText(hoverContent))
                                //#else
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverContent))
                        //#endif
                );

        Component full = Component.translatable("igny.logger.piston.action.failed", pistonPart, failPart);
        logger.log(() -> new Component[]{full});
    }
}