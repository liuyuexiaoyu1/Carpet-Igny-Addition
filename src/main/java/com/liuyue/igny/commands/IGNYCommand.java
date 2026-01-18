package com.liuyue.igny.commands;

import com.liuyue.igny.network.packet.block.HighlightPayload;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
//#if MC < 12005
//$$ import com.liuyue.igny.IGNYServer;
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import net.minecraft.network.FriendlyByteBuf;
//#endif

public class IGNYCommand {

    public static void register(CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("igny")
                        .then(Commands.literal("highlight")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(IGNYCommand::executeHighlight)
                                )
                        )
        );
    }

    private static int executeHighlight(CommandContext<net.minecraft.commands.CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        //#if MC < 12005
        //$$ FriendlyByteBuf buf = PacketByteBufs.create();
        //$$ buf.writeBlockPos(pos);
        //$$ buf.writeInt(0x1655FFFF);
        //$$ buf.writeInt(200);
        //$$ buf.writeBoolean(false);
        //#endif
        ServerPlayNetworking.send(player,
                //#if MC >= 12005
                new HighlightPayload(pos, 0x1655FFFF, 200, false)
                //#else
                //$$ IGNYServer.HIGHLIGHT_PACKET_ID, buf
                //#endif
        );

        return 1;
    }
}