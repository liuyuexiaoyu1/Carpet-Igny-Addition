package com.liuyue.igny.mixins.rule.optimizedTNTErrorScopeFix;

import carpet.CarpetSettings;
import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

//#if MC >= 12102
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.level.ServerExplosion;
//$$ import java.util.List;
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
    @Shadow @Final @Nullable
    private Entity source;

    @Unique
    private static final Object igny$lock = new Object();

    @WrapMethod(
            //#if MC < 12102
            method = "explode"
            //#else
            //$$ method = "calculateExplodedPositions"
            //#endif
    )
    private
            //#if MC >= 12102
            //$$ List<BlockPos>
            //#else
            void
            //#endif
    onExplosionAHighPriority(
            //#if MC < 12102
            Operation<Void> original
            //#else
            //$$ Operation<List<BlockPos>> original
            //#endif
    ) {
        synchronized (igny$lock) {
            boolean changed = CarpetSettings.optimizedTNT;
            if (changed && IGNYSettings.OPTIMIZED_TNT_ERROR_SCOPE_FIX.value()) {
                CarpetSettings.optimizedTNT = this.source instanceof PrimedTnt;
            }
            try {
                //#if MC >= 12102
                //$$ return original.call();
                //#else
                original.call();
                //#endif
            } finally {
                CarpetSettings.optimizedTNT = changed;
            }
        }

    }

    //#if MC < 12102
    @WrapMethod(
            method = "finalizeExplosion"
    )
    private void onExplosionBHighPriority(boolean bl, Operation<Void> original) {
        synchronized (igny$lock) {
            boolean changed = CarpetSettings.optimizedTNT;
            if (changed && IGNYSettings.OPTIMIZED_TNT_ERROR_SCOPE_FIX.value()) {
                CarpetSettings.optimizedTNT = this.source instanceof PrimedTnt;
            }
            try {
                original.call(bl);
            } finally {
                CarpetSettings.optimizedTNT = changed;
            }
        }

    }
    //#endif
}