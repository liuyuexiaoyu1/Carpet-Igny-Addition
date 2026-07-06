package com.liuyue.igny.mixins.rule.entityIDCollisionReintroduce;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    private int id;

    @Inject(method = "getId", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V"), cancellable = true)
    private void getId(CallbackInfoReturnable<Integer> cir) {
        if (IGNYSettings.ENTITY_ID_COLLISION_REINTRODUCE.value()) {
            cir.setReturnValue(this.id);
        }
    }
}
