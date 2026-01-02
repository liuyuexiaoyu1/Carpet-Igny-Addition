package com.liuyue.igny.exception;

import com.liuyue.igny.IGNYServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;

public class IAEUpdateSuppressException extends IllegalArgumentException{
    public IAEUpdateSuppressException(String message) {
        super(message);
    }
}
