package com.liuyue.igny.mixins.rule.hugeMushroomDestroyUnbreakableReintroduce;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractHugeMushroomFeature.class)
public class AbstractHugeMushroomFeatureMixin {
    @WrapOperation(method = "placeMushroomBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"))
    private boolean isAir(BlockState instance, Operation<Boolean> original) {
        if (IGNYSettings.HUGE_MUSHROOM_DESTROY_UNBREAKABLE_REINTRODUCE.value()) {
            return instance.isSolidRender();
        }
        return original.call(instance);
    }

    @WrapOperation(method = "placeMushroomBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean is(BlockState instance, TagKey<?> tagKey, Operation<Boolean> original) {
        if (IGNYSettings.HUGE_MUSHROOM_DESTROY_UNBREAKABLE_REINTRODUCE.value()) {
            return instance.isSolidRender();
        }
        return original.call(instance);
    }
}
