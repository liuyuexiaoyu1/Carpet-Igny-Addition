package com.liuyue.igny.mixins.rule.itemFlowTracker.compat.tis;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(require = @Condition("carpet-tis-addition"))
@Mixin(targets = "carpettisaddition.helpers.rule.largeBarrel.LargeBarrelHelper")
public interface LargeBarrelHelperInvoker {
    @Invoker("isLargeBarrel")
    static boolean igny$isLargeBarrel(BlockState state, Level level, BlockPos pos) {
        throw new AssertionError();
    }

    @Invoker("getOtherPos")
    static BlockPos igny$getOtherPos(BlockState state, Level level, BlockPos pos) {
        throw new AssertionError();
    }
}
