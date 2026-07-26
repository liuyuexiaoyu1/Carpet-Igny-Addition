package com.liuyue.igny.mixins.rule.copperGolemDropOnlyNormal;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CopperGolemStatueBlockEntity.class)
public class CopperGolemStatueBlockEntityMixin {
    @ModifyVariable(method = "getItem", at = @At(value = "HEAD"), argsOnly = true)
    public CopperGolemStatueBlock.Pose modifyPose(CopperGolemStatueBlock.Pose pose) {
        if (IGNYSettings.COPPER_GOLEM_DROP_ONLY_NORMAL.value()) {
            return CopperGolemStatueBlock.Pose.STANDING;
        }
        return pose;
    }
}
