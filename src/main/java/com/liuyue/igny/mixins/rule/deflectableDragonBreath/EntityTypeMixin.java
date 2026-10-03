package com.liuyue.igny.mixins.rule.deflectableDragonBreath;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    //#if 1.20.6..26.1.2
    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void igny$deflectableDragonBreath(TagKey<?> tag, CallbackInfoReturnable<Boolean> cir) {
        EntityType<?> self = (EntityType<?>) (Object) this;
        if (tag == EntityTypeTags.REDIRECTABLE_PROJECTILE
                && self == EntityType.DRAGON_FIREBALL
                && IGNYSettings.DEFLECTABLE_DRAGON_BREATH.value()) {
            cir.setReturnValue(true);
        }
    }
    //#endif
}
