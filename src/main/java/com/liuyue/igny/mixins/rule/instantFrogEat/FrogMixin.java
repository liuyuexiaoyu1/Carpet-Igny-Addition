package com.liuyue.igny.mixins.rule.instantFrogEat;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Frog.class)
public class FrogMixin {
    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void instantEatTick(CallbackInfo ci) {
        Frog frog = (Frog) (Object) this;
        Level level = frog.level();
        if (frog.level().isClientSide() || !IGNYSettings.instantFrogEat || !(level instanceof ServerLevel)) return;
        List<LivingEntity> targets = frog.level().getEntitiesOfClass(
                LivingEntity.class,
                frog.getBoundingBox().inflate(10.0D),
                entity -> Frog.canEat(entity) && entity.isAlive()
        );
        if (!targets.isEmpty()) {
            LivingEntity target = targets.getFirst();
            executeEat((ServerLevel) level, frog, target);
        }
    }

    @Unique
    @SuppressWarnings("unused")
    private void executeEat(ServerLevel level, Frog frog, LivingEntity target) {
        frog.level().playSound(null, frog.getX(), frog.getY(), frog.getZ(),
                net.minecraft.sounds.SoundEvents.FROG_EAT,
                net.minecraft.sounds.SoundSource.NEUTRAL, 2.0F, 1.0F);
        if (target.isAlive()) {
            //#if MC >= 12102
            //$$ frog.doHurtTarget(level, target);
            //#else
            frog.doHurtTarget(target);
            //#endif
            if (!target.isAlive()) {
                target.remove(Entity.RemovalReason.KILLED);
            }
        }
        frog.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
    }
}
