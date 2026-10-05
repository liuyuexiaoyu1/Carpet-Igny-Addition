// MIT License
//
// Copyright (c) 2026 R-Matrix
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

package com.liuyue.igny.mixins.rule.tridentMultipleDamageReintroduced;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.tridentMultipleDamage.PiercingCollisionHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Shadow
    protected abstract boolean canHitEntity(Entity entity);

    @WrapOperation(method = "stepMoveAndHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;findHitEntities(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Ljava/util/Collection;"))
    private Collection<EntityHitResult> igny$collectHitEntities(AbstractArrow instance, Vec3 from, Vec3 to,
                                                                Operation<Collection<EntityHitResult>> original) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(IGNYSettings.TRIDENT_MULTIPLE_DAMAGE_REINTRODUCED.value() && self instanceof ThrownTrident)) {
            return original.call(instance, from, to);
        }
        return PiercingCollisionHelper.collect(instance.level(), instance, from, to,
                instance.getBoundingBox().expandTowards(instance.getDeltaMovement()).inflate(1.0F), this::canHitEntity);
    }
}
