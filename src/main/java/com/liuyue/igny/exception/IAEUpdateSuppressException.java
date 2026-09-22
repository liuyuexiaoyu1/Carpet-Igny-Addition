// MIT License
//
// Copyright (c) 2024 fcsailboat
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.liuyue.igny.exception;

import com.liuyue.igny.IGNYServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;

public class IAEUpdateSuppressException extends IllegalArgumentException {
    private final BlockPos suppressBlockPos;

    public IAEUpdateSuppressException(BlockPos blockPos, String message) {
        super(message);
        this.suppressBlockPos = blockPos;
    }

    public void onCatch(ServerPlayer player, Packet<ServerGamePacketListener> packet) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getGameProfile()
                        .getName() //#replace >= 1.21.10 ? .name()
        ).append("在");
        if (packet instanceof ServerboundPlayerActionPacket actionC2SPacket) {
            switch (actionC2SPacket.getAction()) {
                case START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> builder.append("破坏方块");
                case DROP_ALL_ITEMS, DROP_ITEM -> builder.append("丢弃物品");
                case RELEASE_USE_ITEM -> builder.append("使用物品");
                case SWAP_ITEM_WITH_OFFHAND -> builder.append("交换主副手物品");
                default -> throw new IllegalStateException();
            }
        } else if (packet instanceof ServerboundUseItemOnPacket) {
            builder.append("放置或交互方块");
        } else {
            builder.append("发送").append(packet.getClass().getSimpleName()).append("数据包");
        }
        String levelPos = player.level().dimension().location() + "[" + suppressBlockPos.getX() + "," + suppressBlockPos.getY() + "," + suppressBlockPos.getZ() + "]";
        builder.append("时触发了IAE更新抑制，在").append(levelPos);
        IGNYServer.LOGGER.info(builder.toString());
    }
}