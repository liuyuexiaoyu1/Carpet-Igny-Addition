package com.liuyue.igny.mixins.logger.piston;

import carpet.CarpetSettings;
import carpet.utils.Messenger;
import carpet.utils.Translations;
import com.liuyue.igny.helper.PistonResolveContext;
import com.liuyue.igny.logging.IGNYLoggers;
import com.liuyue.igny.utils.BlockUtil;
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

import java.util.List;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {
    @Shadow @Final private boolean isSticky;

    @Unique
    private MutableComponent cTr(String key) {
        String val = Translations.tr(key, key);
        return Component.literal(val == null ? key : val);
    }

    @Unique
    private String sTr(String key, Object... args) {
        String pattern = Translations.tr(key, key);
        if (pattern == null) return key;
        try {
            return String.format(pattern, args);
        } catch (Exception e) {
            return pattern;
        }
    }

    @Inject(method = "triggerEvent", at = @At("HEAD"))
    private void onTriggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1, CallbackInfoReturnable<Boolean> cir) {
        if (!IGNYLoggers.piston || level.isClientSide()) return;
        carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
        if (logger == null || !logger.hasOnlineSubscribers()) return;
        Direction dir = state.getValue(PistonBaseBlock.FACING);
        if (b0 != 0 && !this.isSticky) {
            handleRetract(logger, level, pos, dir, false, null);
        }
    }

    @Inject(method = "triggerEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private void onTriggerEventRemoveBlock(BlockState state, Level level, BlockPos pos, int b0, int b1, CallbackInfoReturnable<Boolean> cir, @Local Direction direction) {
        if (!IGNYLoggers.piston || level.isClientSide()) return;
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
            MutableComponent hover = Component.empty();
            hover.append(Component.literal(sTr("igny.logger.piston.pulled.blocks", toPull.size())));
            for (BlockPos original : toPull) {
                String name = BlockUtil.getTranslatedName(level.getBlockState(original).getBlock()).getString();
                BlockPos newPos = original.relative(direction.getOpposite());
                hover.append(Component.literal("\n• " + name + " @ " + original.toShortString() + " → " + newPos.toShortString()));
            }
            actionPart = cTr("igny.logger.piston.pull").withStyle(s -> s.withColor(ChatFormatting.LIGHT_PURPLE)
                            //#if MC >= 12105
                            //$$ .withHoverEvent(new HoverEvent.ShowText(hover))
                            //#else
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                    //#endif
            );
        } else {
            Component hover = cTr("igny.logger.piston.no.blocks.moved");
            actionPart = cTr(isSticky ? "igny.logger.piston.pull" : "igny.logger.piston.retract").withStyle(s -> s.withColor(ChatFormatting.GRAY)
                            //#if MC >= 12105
                            //$$ .withHoverEvent(new HoverEvent.ShowText(hover))
                            //#else
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                    //#endif
            );
        }

        logFinal(logger, level, pistonPos, block, actionPart, true);
    }

    @Unique
    private void logFinal(carpet.logging.Logger logger, Level level, BlockPos pos, Block block, Component actionPart, boolean success) {
        Component pistonPart = getPistonPartText(level, pos, block);
        String punctuation = Translations.tr("igny.logger.piston.action.punctuation", "。");
        if (success) {
            String verb = " " + Translations.tr("igny.logger.piston.action.performed_text", "执行了") + " ";
            logger.log(() -> new Component[]{
                    Messenger.c("w ", pistonPart, "w " + verb, actionPart, "w " + punctuation)
            });
        } else {
            logger.log(() -> new Component[]{
                    Messenger.c("w ", pistonPart, " ", actionPart, "w " + punctuation)
            });
        }
    }

    @Unique
    private Component getPistonPartText(Level level, BlockPos pistonPos, Block block) {
        String dimId = level.dimension().location().toString();
        Component hoverText = Component.literal(sTr("igny.logger.piston.hover.dimension_line", dimId))
                .append("\n")
                .append(Component.literal(sTr("igny.logger.piston.hover.position", pistonPos.toShortString())));

        return Component.literal("[")
                .append(BlockUtil.getTranslatedName(block))
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

    @Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    private void pushBlocks(Level level, BlockPos blockPos, Direction direction, boolean extend, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) boolean isExtend, @Local PistonStructureResolver pistonStructureResolver) {
        if (!IGNYLoggers.piston || !(level instanceof ServerLevel)) return;
        carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
        if (logger == null || !logger.hasOnlineSubscribers()) return;
        List<BlockPos> list = pistonStructureResolver.getToPush();
        List<BlockPos> list2 = pistonStructureResolver.getToDestroy();
        if (!isExtend) {
            handleRetract(logger, level, blockPos, direction, this.isSticky, list);
        } else {
            Block block = level.getBlockState(blockPos).getBlock();
            Component actionPart;
            if (!list.isEmpty() || !list2.isEmpty()) {
                MutableComponent hover = Component.empty();
                if (!list.isEmpty()) {
                    hover.append(Component.literal(sTr("igny.logger.piston.pushed.blocks", list.size())));
                    for (BlockPos original : list) {
                        String name = BlockUtil.getTranslatedName(level.getBlockState(original).getBlock()).getString();
                        BlockPos newPos = original.relative(direction);
                        hover.append(Component.literal("\n• " + name + " @ " + original.toShortString() + " → " + newPos.toShortString()));
                    }
                }
                if (!list2.isEmpty()) {
                    if (!list.isEmpty()) {
                        hover.append("\n");
                    }
                    hover.append(Component.literal(sTr("igny.logger.piston.destroyed.blocks", list2.size())));
                    for (BlockPos original : list2) {
                        String name = BlockUtil.getTranslatedName(level.getBlockState(original).getBlock()).getString();
                        hover.append(Component.literal("\n• " + name + " @ " + original.toShortString()));
                    }
                }
                actionPart = cTr("igny.logger.piston.push").withStyle(s -> s.withColor(ChatFormatting.AQUA)
                                //#if MC >= 12105
                                //$$ .withHoverEvent(new HoverEvent.ShowText(hover))
                                //#else
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                        //#endif
                );
            } else {
                Component hover = cTr("igny.logger.piston.no.blocks.moved");
                actionPart = cTr("igny.logger.piston.push").withStyle(s -> s.withColor(ChatFormatting.GRAY)
                                //#if MC >= 12105
                                //$$ .withHoverEvent(new HoverEvent.ShowText(hover))
                                //#else
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                        //#endif
                );
            }
            logFinal(logger, level, blockPos, block, actionPart, true);
        }
    }

    @WrapOperation(method = "checkIfExtend", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z"))
    private boolean wrapResolve(PistonStructureResolver instance, Operation<Boolean> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        PistonResolveContext.startRecording();
        try {
            boolean result = original.call(instance);
            if (IGNYLoggers.piston && !level.isClientSide() && !result) {
                carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
                if (logger != null && logger.hasOnlineSubscribers()) logPistonExtendFailure(logger, level, pos, true);
            }
            return result;
        } finally {
            PistonResolveContext.stopRecording();
        }
    }

    @WrapOperation(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z"))
    private boolean wrapPullResolve(PistonStructureResolver instance, Operation<Boolean> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) boolean extend) {
        if (extend || !IGNYLoggers.piston || level.isClientSide()) return original.call(instance);
        PistonResolveContext.startRecording();
        try {
            boolean result = original.call(instance);
            if (!result) {
                carpet.logging.Logger logger = carpet.logging.LoggerRegistry.getLogger("piston");
                if (logger != null && logger.hasOnlineSubscribers())
                    logPistonExtendFailure(logger, level, blockPos, false);
            }
            return result;
        } finally {
            PistonResolveContext.stopRecording();
        }
    }

    @Unique
    private void logPistonExtendFailure(carpet.logging.Logger logger, Level level, BlockPos pos, boolean isExtend) {
        PistonResolveContext.FailureReason reason = PistonResolveContext.getFailureReason();
        MutableComponent hover = Component.empty();
        if (reason != null) {
            if (reason.type == PistonResolveContext.FailureType.TOO_MANY_BLOCKS) {
                String action = Translations.tr(isExtend ? "igny.logger.piston.push" : "igny.logger.piston.pull", isExtend ? "推出" : "拉回");
                hover.append(Component.literal(sTr("igny.logger.piston.failure.too_many_blocks", action, CarpetSettings.pushLimit)));
            } else if (reason.type == PistonResolveContext.FailureType.UNPUSHABLE_BLOCK && reason.blockPos != null) {
                BlockState bs = level.getBlockState(reason.blockPos);
                if (bs.is(Blocks.MOVING_PISTON)) return;
                hover.append(cTr("igny.logger.piston.failure.unpushable_block")).append(Component.literal("\n• " + BlockUtil.getDisplayName(bs) + " @ " + reason.blockPos.toShortString()));
            } else {
                hover.append(cTr("igny.logger.piston.failure.unknown"));
            }
        } else {
            hover.append(cTr("igny.logger.piston.failure.unknown"));
        }

        Block block = isExtend ? level.getBlockState(pos).getBlock() : (this.isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON);
        String actionKey = isExtend ? "igny.logger.piston.push.failed" : "igny.logger.piston.pull.failed";
        Component failPart = cTr(actionKey).withStyle(s -> s.withColor(ChatFormatting.RED)
                        //#if MC >= 12105
                        //$$ .withHoverEvent(new HoverEvent.ShowText(hover))
                        //#else
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                //#endif
        );

        logFinal(logger, level, pos, block, failPart, false);
    }
}