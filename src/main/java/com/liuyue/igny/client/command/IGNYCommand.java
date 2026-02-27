package com.liuyue.igny.client.command;

import com.liuyue.igny.client.command.argument.ClientBlockPosArgumentType;
import com.liuyue.igny.client.renderer.world.HighlightBlocksRenderer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
//#if MC >= 26.1
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
//#else
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//#endif
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.BlockPos;

public class IGNYCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                //#if MC >= 26.1
                //$$ ClientCommands
                //#else
                ClientCommandManager
                        //#endif
                        .literal("igny")
                        .then(
                                //#if MC >= 26.1
                                //$$ ClientCommands
                                //#else
                                ClientCommandManager
                                        //#endif
                                        .literal("highlight")
                                .then(
                                        //#if MC >= 26.1
                                        //$$ ClientCommands
                                        //#else
                                        ClientCommandManager
                                                //#endif
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
