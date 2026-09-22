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

//#if >= 1.21.2
/*$$import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerExplosion;
import java.util.List;$$*/
//#endif

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
        value = Explosion.class, //#replace >= 1.21.2 ? value = ServerExplosion.class,
        priority = 900
)
public abstract class ExplosionMixin
{
    @Shadow @Final @Nullable
    private Entity source;

    @Unique
    private static final Object igny$lock = new Object();

    @WrapMethod(
            method = "explode" //#replace >= 1.21.2 ? method = "calculateExplodedPositions"
    )
    private
            void //#replace >= 1.21.2 ? List<BlockPos>
    onExplosionAHighPriority(
            Operation<Void> original //#replace >= 1.21.2 ? Operation<List<BlockPos>> original
    ) {
        synchronized (igny$lock) {
            boolean changed = CarpetSettings.optimizedTNT;
            if (changed && IGNYSettings.OPTIMIZED_TNT_ERROR_SCOPE_FIX.value()) {
                CarpetSettings.optimizedTNT = this.source instanceof PrimedTnt;
            }
            try {
                original.call(); //#replace >= 1.21.2 ? return original.call();
            } finally {
                CarpetSettings.optimizedTNT = changed;
            }
        }

    }

    //#if < 1.21.2
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