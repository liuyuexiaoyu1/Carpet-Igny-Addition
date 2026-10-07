package com.liuyue.igny.mixins.rule.legacyBounceReintroduced;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public boolean verticalCollision;

    @Shadow
    protected abstract double getEntityBounciness();

    @Inject(method = "restituteMovementAfterCollisions", at = @At(value = "HEAD"), cancellable = true)
    private void restituteMovementAfterCollisions(BlockState effectState, boolean xCollision, boolean zCollision, Vec3 movement, CallbackInfo ci) {
        if (!IGNYSettings.LEGACY_BOUNCE_REINTRODUCED.value()) {
            return;
        }

        final Entity self = (Entity) (Object) this;

        if (xCollision || zCollision) {
            final Vec3 current = self.getDeltaMovement();
            self.setDeltaMovement(xCollision ? 0.0 : current.x, current.y, zCollision ? 0.0 : current.z);
        }

        if (!this.verticalCollision) {
            return;
        }

        if (self.isSuppressingBounce() || effectState.is(BlockTags.SUPPRESSES_BOUNCE)) {
            return;
        }

        double blockBounciness = effectState.getBlock().getBounceRestitution();
        if (!(self instanceof LivingEntity)) {
            blockBounciness *= 0.8;
        }

        final double restitution = Math.max(this.getEntityBounciness(), blockBounciness);
        final Vec3 delta = self.getDeltaMovement();
        if (restitution > 0.0 && delta.y < 0.0) {
            self.setDeltaMovement(delta.x, -delta.y * restitution, delta.z);
        }
        ci.cancel();
    }
}
