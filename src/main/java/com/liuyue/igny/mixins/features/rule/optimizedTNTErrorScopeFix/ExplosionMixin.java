package com.liuyue.igny.mixins.features.rule.optimizedTNTErrorScopeFix;

import carpet.CarpetSettings;
import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//#if MC >= 12102
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.level.ServerExplosion;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$ import java.util.List;
//#else
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
        //#if MC >= 12102
        //$$ value = ServerExplosion.class,
        //#else
        value = Explosion.class,
        //#endif
        priority = 900
)
public abstract class ExplosionMixin
{
    //#if MC < 12102
    @Shadow @Final @Nullable
    private Entity source;

    @Unique
    boolean original;

    @Inject(
            method = "explode",
            at = @At("HEAD")
    )
    private void onExplosionAHighPriority(
            CallbackInfo ci
    )
    {
        this.original = CarpetSettings.optimizedTNT;
        if (IGNYSettings.optimizedTNTErrorScopeFix) {
            CarpetSettings.optimizedTNT = this.source instanceof PrimedTnt;
        }
    }

    @Inject(
            method = "explode",
            at = @At("TAIL")
    )
    private void onExplosionAHighPriorityB(
            CallbackInfo ci
    )
    {
        if (IGNYSettings.optimizedTNTErrorScopeFix) {
            CarpetSettings.optimizedTNT = this.original;
        }
    }


    @Inject(
            method = "finalizeExplosion",
            at = @At("HEAD")
    )
    private void onExplosionBHighPriority(CallbackInfo ci)
    {
        this.original = CarpetSettings.optimizedTNT;
        if (IGNYSettings.optimizedTNTErrorScopeFix) {
            CarpetSettings.optimizedTNT = this.source instanceof PrimedTnt;
        }
    }

    @Inject(
            method = "finalizeExplosion",
            at = @At("TAIL")
    )
    private void onExplosionBHighPriorityB(CallbackInfo ci)
    {
        if (IGNYSettings.optimizedTNTErrorScopeFix) {
            CarpetSettings.optimizedTNT = this.original;
        }
    }
    //#endif
}