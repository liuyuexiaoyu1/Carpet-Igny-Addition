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
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.tridentMultipleDamage.PiercingCollisionHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    protected AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract boolean canHitEntity(Entity entity);

    @Unique
    private float igny$preHitYaw;
    @Unique
    private float igny$preHitPitch;
    @Unique
    private Vec3 igny$anchorPos;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;findHitEntity(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/EntityHitResult;"))
    private EntityHitResult igny$collectHitEntities(AbstractArrow instance, Vec3 from, Vec3 to,
                                                     Operation<EntityHitResult> original,
                                                     @Share("igny$hitEntities") LocalRef<ArrayList<EntityHitResult>> hitEntities) {
        this.igny$anchorPos = null;
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(IGNYSettings.TRIDENT_MULTIPLE_DAMAGE_REINTRODUCED.value() && self instanceof ThrownTrident)) {
            return original.call(instance, from, to);
        }
        ArrayList<EntityHitResult> list = new ArrayList<>(this.igny$collectPiercingCollisions(from, to));
        list.sort(Comparator.comparingDouble(hit -> from.distanceToSqr(hit.getEntity().position())));
        hitEntities.set(list);
        return list.isEmpty() ? null : list.getFirst();
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
    private void igny$hitAllEntities(AbstractArrow instance, HitResult hitResult, Operation<Void> original, @Share("igny$hitEntities") LocalRef<ArrayList<EntityHitResult>> hitEntities) {
        ArrayList<EntityHitResult> list = hitEntities.get();
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(IGNYSettings.TRIDENT_MULTIPLE_DAMAGE_REINTRODUCED.value() && self instanceof ThrownTrident) || list == null || list.isEmpty()) {
            original.call(instance, hitResult);
            return;
        }
        Vec3 velocity = instance.getDeltaMovement();
        this.igny$preHitYaw = (float) (Mth.atan2(velocity.x, velocity.z) * 57.2957763671875D);
        this.igny$preHitPitch = (float) (Mth.atan2(velocity.y, velocity.horizontalDistance()) * 57.2957763671875D);
        if (this.igny$anchorPos == null) {
            this.igny$anchorPos = hitResult.getLocation();
        }
        instance.setPos(this.igny$anchorPos);
        for (EntityHitResult hitResult2 : list) {
            original.call(instance, hitResult2);
            if (!instance.isAlive()) {
                return;
            }
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setYRot(F)V", ordinal = 1))
    private void igny$keepYaw(AbstractArrow instance, float yaw, Operation<Void> original) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if ((IGNYSettings.TRIDENT_MULTIPLE_DAMAGE_REINTRODUCED.value() && self instanceof ThrownTrident) && this.igny$anchorPos != null) {
            original.call(instance, this.igny$preHitYaw);
        } else {
            original.call(instance, yaw);
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setXRot(F)V", ordinal = 1))
    private void igny$keepPitch(AbstractArrow instance, float pitch, Operation<Void> original) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if ((IGNYSettings.TRIDENT_MULTIPLE_DAMAGE_REINTRODUCED.value() && self instanceof ThrownTrident) && this.igny$anchorPos != null) {
            original.call(instance, this.igny$preHitPitch);
        } else {
            original.call(instance, pitch);
        }
    }

    @Unique
    private Collection<EntityHitResult> igny$collectPiercingCollisions(Vec3 from, Vec3 to) {
        return PiercingCollisionHelper.collect(this.level(), this, from, to,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0F), this::canHitEntity);
    }
}
