package com.liuyue.igny.mixins.rule.sequentialArrowHitEntityReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    public AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Unique EntityHitResult entityHitResult;

    @Shadow
    protected abstract @Nullable EntityHitResult findHitEntity(Vec3 start, Vec3 end);

    @WrapOperation(method = "stepMoveAndHit", at = @At(value = "NEW", target = "(Ljava/util/Collection;)Ljava/util/ArrayList;"))
    private ArrayList<?> onListInit(Collection<?> c, Operation<ArrayList<?>> original) {
        if (IGNYSettings.SEQUENTIAL_ARROW_HIT_ENTITY_REINTRODUCED.value()) {
            return new ArrayList<>();
        }
        return original.call(c);
    }

    @WrapOperation(method = "stepMoveAndHit", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;sort(Ljava/util/Comparator;)V"))
    private void sort(ArrayList<?> instance, Comparator<?> c, Operation<Void> original) {
        if (IGNYSettings.SEQUENTIAL_ARROW_HIT_ENTITY_REINTRODUCED.value()) {
            return;
        }
        original.call(instance, c);
    }

    @ModifyVariable(method = "stepMoveAndHit",at = @At(value = "STORE"))
    private EntityHitResult onModifyList(EntityHitResult original, @Local(ordinal = 0) Vec3 pos, @Local(argsOnly = true) BlockHitResult result) {
        if (IGNYSettings.SEQUENTIAL_ARROW_HIT_ENTITY_REINTRODUCED.value()) {
            entityHitResult = this.findHitEntity(pos, result.getLocation());
            return entityHitResult;
        }
        return original;
    }

    @WrapOperation(method = "stepMoveAndHit", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;isEmpty()Z"))
    private boolean isEmpty(ArrayList<?> instance, Operation<Boolean> original) {
        if (IGNYSettings.SEQUENTIAL_ARROW_HIT_ENTITY_REINTRODUCED.value()) {
            return entityHitResult == null;
        }
        return original.call(instance);
    }

    @WrapOperation(method = "stepMoveAndHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;hitTargetsOrDeflectSelf(Ljava/util/Collection;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection hitTargetsOrDeflectSelf(AbstractArrow instance, Collection<EntityHitResult> hitResults, Operation<ProjectileDeflection> original) {
        if (IGNYSettings.SEQUENTIAL_ARROW_HIT_ENTITY_REINTRODUCED.value()) {
            EntityHitResult result = entityHitResult;
            entityHitResult = null;
            return this.hitTargetOrDeflectSelf(result);
        }
        return original.call(instance, hitResults);
    }
}
