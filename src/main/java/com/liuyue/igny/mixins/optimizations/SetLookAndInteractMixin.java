// MIT License
//
// Copyright (c) 2025 Melationin
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

package com.liuyue.igny.mixins.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.optimizations.IEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SetLookAndInteract.class)
public class SetLookAndInteractMixin {
    @Inject(
            //#if MC >= 26.1
            //$$ method = "lambda$create$2"
            //#else
            method = "method_47085"
            //#endif
            , at = @At(value = "HEAD"), cancellable = true)
    private static void create(BehaviorBuilder.Instance<?> instance, MemoryAccessor<?, ?> memoryAccessor, int i, EntityType<?> entityType, MemoryAccessor<?, ?> memoryAccessor2, MemoryAccessor<?, ?> memoryAccessor3, ServerLevel serverLevel, LivingEntity livingEntity, long l, CallbackInfoReturnable<Boolean> cir){
        if (livingEntity instanceof Piglin && ((IEntity)livingEntity).carpet_Igny_Addition$getCrammingCount() >= IGNYSettings.optimizedEntityLimit){
            cir.setReturnValue(false);
        }
    }
}
