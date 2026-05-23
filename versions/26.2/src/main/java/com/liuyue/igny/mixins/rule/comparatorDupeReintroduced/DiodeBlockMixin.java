package com.liuyue.igny.mixins.rule.comparatorDupeReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DiodeBlock.class)
public class DiodeBlockMixin {
    @WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private boolean neighborChanged(BlockState instance, Object o, Operation<Boolean> original) {
        return IGNYSettings.COMPARATOR_DUPE_REINTRODUCED.value() || original.call(instance, o);
    }
}
