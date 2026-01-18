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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @Inject(method = "wantsToPickup",at=@At(value = "HEAD"),cancellable = true)
    private static void wantsToPickup(Piglin piglin, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir){
        ResourceLocation resourceLocation = EntityType.getKey(piglin.getType());
        String entityTypeName = resourceLocation.toString();
        if (IGNYSettings.CRAMMING_ENTITIES.contains(entityTypeName)) {
            if (itemStack.getItem() == Items.GOLD_INGOT) {
                return;
            }
            if (((IEntity) piglin).carpet_Igny_Addition$getCrammingCount() >= IGNYSettings.optimizedEntityLimit) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "isPlayerHoldingLovedItem",at=@At(value = "HEAD"),cancellable = true)
    private static void isPlayerHoldingLovedItem(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir){
        if (((IEntity)livingEntity).carpet_Igny_Addition$getCrammingCount() >= IGNYSettings.optimizedEntityLimit) {
            cir.setReturnValue(false);
        }
    }
}
