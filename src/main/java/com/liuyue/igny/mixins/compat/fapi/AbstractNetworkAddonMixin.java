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

package com.liuyue.igny.mixins.compat.fapi;

import carpet.patches.FakeClientConnection;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.mixins.compat.accessor.fapi.AbstractChanneledNetworkAddonAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import net.fabricmc.fabric.impl.networking.GlobalReceiverRegistry;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = AbstractNetworkAddon.class, priority = 980, remap = false)
public abstract class AbstractNetworkAddonMixin {
    //#if MC <= 12002
    @WrapOperation(method = "lateInit", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/networking/GlobalReceiverRegistry;startSession(Lnet/fabricmc/fabric/impl/networking/AbstractNetworkAddon;)V"))
    private void notStartSession_ifFakeClientConnection(GlobalReceiverRegistry<?> instance, AbstractNetworkAddon<?> addon, Operation<Void> original) {
        if (IGNYSettings.fakePlayerSpawnMemoryLeakFix.get() && addon instanceof AbstractChanneledNetworkAddon<?>) {
            Connection connection = ((AbstractChanneledNetworkAddonAccessor) addon).getConnection();
            if (connection instanceof FakeClientConnection) {
                return;
            }
        }
        original.call(instance, addon);
    }
    //#endif
}
