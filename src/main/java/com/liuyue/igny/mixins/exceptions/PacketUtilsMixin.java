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

package com.liuyue.igny.mixins.exceptions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.ReportedException;
import com.liuyue.igny.exception.IAEUpdateSuppressException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

//?>= 1.21.9 ? import net.minecraft.network.PacketProcessor;

@Mixin(PacketUtils.class)
public class PacketUtilsMixin {
    @SuppressWarnings("unchecked")
    //#if >= 1.21.9
    /*$$@WrapOperation(method = "ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketProcessor;scheduleIfPossible(Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/protocol/Packet;)V"))
    private static <T extends PacketListener> void exceptionReason(PacketProcessor instance, T listener, Packet<T> packet, Operation<Void> original) {$$*/
    //#else
    @WrapOperation(method = "method_11072", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V"))
    private static <T extends PacketListener> void exceptionReason(final Packet<T> packet, final T listener, Operation<Void> original) {
        //#endif
        try {
            original.call(packet, listener); //#replace >= 1.21.9 ? original.call(instance, listener, packet);
        } catch (RuntimeException e) {
            IAEUpdateSuppressException iae;
            if (e instanceof IAEUpdateSuppressException) {
                iae = (IAEUpdateSuppressException) e;
            } else if (e instanceof ReportedException crashException && crashException.getCause() instanceof IAEUpdateSuppressException exception) {
                iae = exception;
            } else {
                throw e;
            }
            exceptionReason((Packet<ServerGamePacketListener>) packet, listener, iae);
            throw e;
        }
    }

    @Unique
    private static <T extends PacketListener> void exceptionReason(Packet<ServerGamePacketListener> packet, T listener, IAEUpdateSuppressException iaeUpdateSuppressException) {
        if (listener instanceof ServerGamePacketListenerImpl networkHandler) {
            iaeUpdateSuppressException.onCatch(networkHandler.player, packet);
        }
    }
}