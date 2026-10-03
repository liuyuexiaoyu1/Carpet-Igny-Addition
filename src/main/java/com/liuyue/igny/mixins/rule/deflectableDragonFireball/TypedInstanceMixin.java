package com.liuyue.igny.mixins.rule.deflectableDragonFireball;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.projectile.DragonFireball; //#replace >= 1.21.11 ? import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.core.TypedInstance")
public interface TypedInstanceMixin {

    //#if >= 26.1.2
    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void igny$deflectableDragonBreath(TagKey<?> tag, CallbackInfoReturnable<Boolean> cir) {
        if (tag == EntityTypeTags.REDIRECTABLE_PROJECTILE
                && (Object) this instanceof DragonFireball
                && IGNYSettings.DEFLECTABLE_DRAGON_FIREBALL.value()) {
            cir.setReturnValue(true);
        }
    }
    //#endif
}
