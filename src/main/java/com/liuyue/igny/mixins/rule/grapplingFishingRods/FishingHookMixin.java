package com.liuyue.igny.mixins.rule.grapplingFishingRods;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.grapplingFishingRods.GrappleFallGuard;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
//? >= 1.21.11 ? import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
//? == 1.21.3 ? import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V", //#replace == 1.21.3 ? method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;IILnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "RETURN"))
    private void igny$boostThrow(Player player, Level level, int i, int j, CallbackInfo ci) //#replace == 1.21.3 ? private void igny$boostThrow(Player player, Level level, int i, int j, ItemStack itemStack, CallbackInfo ci)
    {
        this.igny$boost();
    }

    @Unique
    private void igny$boost() {
        if (!IGNYSettings.GRAPPLING_FISHING_RODS.value()) {
            return;
        }
        FishingHook self = (FishingHook) (Object) this;
        self.setDeltaMovement(self.getDeltaMovement().scale(1.8));
    }

    @WrapOperation(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/FishingHook;shouldStopFishing(Lnet/minecraft/world/entity/player/Player;)Z"
    ))
    private boolean igny$grappleWhenLineBreaks(FishingHook instance, Player player, Operation<Boolean> original) {
        boolean stop = original.call(instance, player);
        if (stop && IGNYSettings.GRAPPLING_FISHING_RODS.value()) {
            igny$grapple(instance, player);
        }
        return stop;
    }

    //#if >= 26.4
    /*$$@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;addDeltaMovement(DDD)V"))
    private void igny$skipGravity(FishingHook instance, double x, double y, double z, Operation<Void> original)$$*/
    //#else
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 igny$skipGravity(Vec3 instance, double x, double y, double z, Operation<Vec3> original)
    //#endif
    {
        if (IGNYSettings.GRAPPLING_FISHING_RODS.value() && x == 0.0 && y == -0.03 && z == 0.0) {
            return instance; //#replace >= 26.4 ? original.call(instance, 0d, 0d, 0d);
            //? >= 26.4 ? return;
        }
        return original.call(instance, x, y, z); //#replace >= 26.4 ? original.call(instance, x, y, z);
    }

    @WrapOperation(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"
    ))
    private Vec3 igny$skipDamping(Vec3 instance, double factor, Operation<Vec3> original) {
        if (IGNYSettings.GRAPPLING_FISHING_RODS.value() && factor == 0.92) {
            return instance;
        }
        return original.call(instance, factor);
    }

    @ModifyConstant(method = "shouldStopFishing", constant = @Constant(doubleValue = 1024.0))
    private double igny$extendRange(double original) {
        return IGNYSettings.GRAPPLING_FISHING_RODS.value() ? original * 5 : original;
    }

    @WrapOperation(method = "retrieve", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/FishingHook;shouldStopFishing(Lnet/minecraft/world/entity/player/Player;)Z"
    ))
    private boolean igny$grappleOnRetrieve(FishingHook instance, Player player, Operation<Boolean> original) {
        boolean stop = original.call(instance, player);
        if (!stop && IGNYSettings.GRAPPLING_FISHING_RODS.value()) {
            igny$grapple(instance, player);
        }
        return stop;
    }

    @Unique
    private static void igny$grapple(FishingHook hook, Player player) {
        Entity hooked = hook.getHookedIn();
        Level level = hook.level(); //#replace < 1.20.1 ? Level level = hook.level;
        Vec3 target = null;
        if (hooked != null) {
            target = hooked.position();
        } else if (!level.noCollision(hook.getBoundingBox().inflate(0.5))) {
            target = hook.position();
        }
        else if (level.getWorldBorder().getDistanceToBorder(hook.getX(), hook.getZ()) < 0.5) //#replace >= 1.21.11 ? else if (level instanceof ServerLevel serverLevel && serverLevel.getWorldBorder().getDistanceToBorder(hook.getX(), hook.getZ()) < 0.5)
        {
            target = hook.position();
        }
        if (target == null) {
            return;
        }
        Vec3 pull = target.subtract(player.position());
        if (pull.length() <= 0.01) {
            return;
        }
        player.addDeltaMovement(pull.normalize().scale(3.0).add(0.0, 0.4, 0.0));

        if (player instanceof GrappleFallGuard guard) {
            guard.igny$setGrappleFallGuard(true);
        }

        //#if < 1.21.11
        player.hasImpulse = true;
        //#endif
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
        }
    }
}
