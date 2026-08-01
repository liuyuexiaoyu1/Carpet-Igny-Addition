package com.liuyue.igny.mixins.logger.allFurnace;

import com.liuyue.igny.utils.interfaces.allFurnace.SleepingBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    @Shadow
    @Nullable
    protected Level level;

    @Inject(method = "setChanged()V", at = @At(value = "RETURN"))
    private void onSetChanged(CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        if (blockEntity instanceof SleepingBlock sleepingBlock && sleepingBlock.igny$isSleeping() && this.level != null && !this.level.isClientSide()) {
         sleepingBlock.igny$setSleeping(false);
        }
    }
}
