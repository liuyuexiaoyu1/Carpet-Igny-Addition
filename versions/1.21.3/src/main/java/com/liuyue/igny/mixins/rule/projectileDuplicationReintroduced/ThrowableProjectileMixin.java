package com.liuyue.igny.mixins.rule.projectileDuplicationReintroduced;



import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrowableProjectile.class)
public abstract class ThrowableProjectileMixin extends Projectile {
    public ThrowableProjectileMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Shadow protected abstract void handleFirstTickBubbleColumn();

    @Shadow protected abstract void applyInertia();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void OldTick(CallbackInfo ci) {
        if (IGNYSettings.projectileDuplicationReintroduced) {
            ThrowableProjectile self = (ThrowableProjectile) (Object) this;
            if (!(self instanceof ThrownEnderpearl)) {
                this.handleFirstTickBubbleColumn();
                super.tick();
                HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(self, this::canHitEntity);
                if (hitResult.getType() != HitResult.Type.MISS) {
                    this.hitTargetOrDeflectSelf(hitResult);
                }
                Vec3 vec = this.getDeltaMovement();
                double nx = this.getX() + vec.x;
                double ny = this.getY() + vec.y;
                double nz = this.getZ() + vec.z;
                this.updateRotation();
                this.applyInertia();
                this.applyGravity();
                this.setPos(nx, ny, nz);
                //#if MC>=12105
                //$$ ((EntityInvoker) self).invokeApplyEffectsFromBlocks();
                //#else
                this.applyEffectsFromBlocks();
                //#endif
                ci.cancel();
            }

        }
    }
}
