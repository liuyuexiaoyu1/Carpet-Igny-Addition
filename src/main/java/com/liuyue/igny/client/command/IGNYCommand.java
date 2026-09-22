package com.liuyue.igny.client.command;

import com.liuyue.igny.client.command.argument.ClientBlockPosArgumentType;
import com.liuyue.igny.client.renderer.world.HighlightBlocksRenderer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager; //#replace >= 26.1 ? import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.BlockPos;

public class IGNYCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager //#replace >= 26.1 ? ClientCommands
                        .literal("igny")
                        .then(
                                ClientCommandManager //#replace >= 26.1 ? ClientCommands
                                        .literal("highlight")
                                .then(
                                        ClientCommandManager //#replace >= 26.1 ? ClientCommands
                                        .argument("blockPos", ClientBlockPosArgumentType.blockPos())
                                        .executes(IGNYCommand::executeHighlight)
                                )
                        )
        );
    }

    private static int executeHighlight(CommandContext<FabricClientCommandSource> context) {
        BlockPos pos = ClientBlockPosArgumentType.getBlockPos(context, "blockPos");
        HighlightBlocksRenderer.addHighlight(pos, 0x1655FFFF, 200, false);
        return 1;
    }
}
