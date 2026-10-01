package com.liuyue.igny.mixins.rule.fakePlayerNoCollision;

import com.liuyue.igny.utils.interfaces.fakePlayerNoCollision.NoCollisionFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "HEAD"), cancellable = true)
    private void onPush(Entity entity, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof Player) || !(entity instanceof Player)) {
            return;
        }
        if (igny$noCollision(self) || igny$noCollision(entity)) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean igny$noCollision(Entity entity) {
        return entity instanceof NoCollisionFlag flag && flag.igny$isNoCollision();
    }
}
