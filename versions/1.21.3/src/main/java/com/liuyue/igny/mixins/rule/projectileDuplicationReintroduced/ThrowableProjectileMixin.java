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
                HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(self, ((ProjectileInvoker) self)::invokeCanHitEntity);
                if (hitResult.getType() != HitResult.Type.MISS) {
                    ((ProjectileInvoker) self).invokeHitTargetOrDeflectSelf(hitResult);
                }
                Vec3 vec = self.getDeltaMovement();
                double nx = self.getX() + vec.x;
                double ny = self.getY() + vec.y;
                double nz = self.getZ() + vec.z;
                ((ProjectileInvoker) self).invokeUpdateRotation();
                this.applyInertia();
                ((EntityInvoker) self).invokeApplyGravity();
                self.setPos(nx, ny, nz);
                //#if MC>=12105
                //$$ ((EntityInvoker) self).invokeApplyEffectsFromBlocks();
                //#else
                self.applyEffectsFromBlocks();
                //#endif
                ci.cancel();
            }

        }
    }
}
