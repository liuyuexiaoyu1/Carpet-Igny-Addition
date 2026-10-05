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

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.tridentMultipleDamage.PiercingCollisionHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    protected AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract boolean canHitEntity(Entity entity);

    @ModifyVariable(method = "stepMoveAndHit", at = @At("LOAD"), ordinal = 0)
    private EntityHitResult igny$collectHitEntities(EntityHitResult original, BlockHitResult blockHitResult,
                                                    @Share("igny$hitEntities") LocalRef<ArrayList<EntityHitResult>> hitEntities) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(IGNYSettings.TRIDENT_MULTIPLE_DAMAGE_REINTRODUCED.value() && self instanceof ThrownTrident)) {
            return original;
        }
        Vec3 from = this.position();
        ArrayList<EntityHitResult> list = new ArrayList<>(this.igny$collectPiercingCollisions(from, blockHitResult.getLocation()));
        list.sort(Comparator.comparingDouble(hit -> from.distanceToSqr(hit.getEntity().position())));
        hitEntities.set(list);
        return list.isEmpty() ? null : list.get(0);
    }

    @Expression("? == null")
    @ModifyExpressionValue(method = "stepMoveAndHit", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
    private boolean igny$noHitEntity(boolean original, @Share("igny$hitEntities") LocalRef<ArrayList<EntityHitResult>> hitEntities) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(IGNYSettings.TRIDENT_MULTIPLE_DAMAGE_REINTRODUCED.value() && self instanceof ThrownTrident)) {
            return original;
        }
        return hitEntities.get().isEmpty();
    }

    @WrapOperation(method = "stepMoveAndHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;",
            ordinal = 1))
    private ProjectileDeflection igny$hitAllEntities(AbstractArrow instance, HitResult hitResult, Operation<ProjectileDeflection> original, @Share("igny$hitEntities") LocalRef<ArrayList<EntityHitResult>> hitEntities) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(IGNYSettings.TRIDENT_MULTIPLE_DAMAGE_REINTRODUCED.value() && self instanceof ThrownTrident)) {
            return original.call(instance, hitResult);
        }
        return this.igny$hitOrDeflect(hitEntities.get());
    }

    @Unique
    private ProjectileDeflection igny$hitOrDeflect(Collection<EntityHitResult> hitResults) {
        for (EntityHitResult hitResult : hitResults) {
            ProjectileDeflection deflection = this.hitTargetOrDeflectSelf(hitResult);
            if (!this.isAlive() || deflection != ProjectileDeflection.NONE) {
                return deflection;
            }
        }
        return ProjectileDeflection.NONE;
    }

    @Unique
    private Collection<EntityHitResult> igny$collectPiercingCollisions(Vec3 from, Vec3 to) {
        return PiercingCollisionHelper.collect(this.level(), this, from, to,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0F), this::canHitEntity);
    }
}
