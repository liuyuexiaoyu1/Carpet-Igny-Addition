package com.liuyue.igny.commands;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.CommandPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FixnotepitchCommmand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("fixnotepitch")
                        .requires(source -> CommandPermissions.canUseCommand(source, IGNYSettings.commandFixnotepitch))
                        .then(Commands.argument("pos1", BlockPosArgument.blockPos())
                                .then(Commands.argument("pos2", BlockPosArgument.blockPos())
                                        .executes(context -> executeCommand(context, 0))
                                        .then(Commands.argument("pitch", IntegerArgumentType.integer(0, 24))
                                                .executes(context ->
                                                        executeCommand(
                                                                context, IntegerArgumentType.getInteger(context, "pitch")
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int executeCommand(CommandContext<CommandSourceStack> context, int targetPitch) throws CommandSyntaxException {
        final BlockPos pos1 = BlockPosArgument.getLoadedBlockPos(context, "pos1");
        final BlockPos pos2 = BlockPosArgument.getLoadedBlockPos(context, "pos2");
        final CommandSourceStack source = context.getSource();
        final Level level = source.getLevel();

        final int minX = Math.min(pos1.getX(), pos2.getX());
        final int minY = Math.min(pos1.getY(), pos2.getY());
        final int minZ = Math.min(pos1.getZ(), pos2.getZ());
        final int maxX = Math.max(pos1.getX(), pos2.getX());
        final int maxY = Math.max(pos1.getY(), pos2.getY());
        final int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        final int changedCount = calculateNoteBlockChanges(level, minX, minY, minZ, maxX, maxY, maxZ, targetPitch);

        source.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                        Component.translatable("igny.command.fixnotepitch.success", changedCount, targetPitch),
                true
        );

        return changedCount;
    }

    private static int calculateNoteBlockChanges(Level level, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int targetPitch) {
        int changedCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.isLoaded(pos)) {
                        BlockState state = level.getBlockState(pos);
                        if (state.is(Blocks.NOTE_BLOCK) && state.hasProperty(BlockStateProperties.NOTE)) {
                            int currentNote = state.getValue(BlockStateProperties.NOTE);
                            if (currentNote != targetPitch) {
                                BlockState newState = state.setValue(BlockStateProperties.NOTE, targetPitch);
                                if (IGNYSettings.fixnotepitchUpdateBlock) {
                                    level.setBlock(pos, newState, 3);
                                } else {
                                    level.setBlock(pos, newState, 2 | 16);
                                }
                                changedCount++;
                            }
                        }
                    }
                }
            }
        }
        return changedCount;
    }
}